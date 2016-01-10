#include "stdafx.h"
#include "CommandRunner.h"

/* 
This class runs the VM command provided to it via the runComamnd Function
Author : Swati Garg
*/

CommandRunner::CommandRunner(string vmFileName, string vmLoginName, string vmPassword, bool fRuningVMOnly, bool fInteractive)
{
	this->vmFileName = vmFileName;
	this->vmLoginName = vmLoginName;
	this->vmPassword = vmPassword;
	this->fRuningVMOnly = fRuningVMOnly;
	this->fInteractive = fInteractive;
}

CommandRunner::~CommandRunner()
{
}

bool CommandRunner::connectVIX(VixHandle* phostHandle)
{
	VixHandle hostHandle;
	VixHandle jobHandle = VixHost_Connect(VIX_API_VERSION, VIX_SERVICEPROVIDER_DEFAULT, NULL, 0, NULL, NULL, 0,
								VIX_INVALID_HANDLE, NULL, NULL);
	VixError err = VixJob_Wait(jobHandle, VIX_PROPERTY_JOB_RESULT_HANDLE, phostHandle, VIX_PROPERTY_NONE);
	Vix_GetProperties(jobHandle, VIX_PROPERTY_JOB_RESULT_HANDLE, &hostHandle, VIX_PROPERTY_NONE);
	Vix_ReleaseHandle(jobHandle);
	if (VIX_FAILED(err)) {
		fprintf(stderr, "failed to connect to host (%"FMT64"d %s)\n", err, Vix_GetErrorText(err, NULL));
		return false;
	}
	return true;
}

bool CommandRunner::openVM(VixHandle hostHandle, VixHandle* pvmHandle)
{
	VixHandle jobHandle = VixHost_OpenVM(hostHandle, vmFileName.c_str(), VIX_VMOPEN_NORMAL, VIX_INVALID_HANDLE, NULL, NULL);
	VixError err = VixJob_Wait(jobHandle, VIX_PROPERTY_JOB_RESULT_HANDLE, pvmHandle, VIX_PROPERTY_NONE);
	Vix_ReleaseHandle(jobHandle);
	if (VIX_FAILED(err)) {
		fprintf(stderr, "failed to connect to vm (%"FMT64"d %s)\n", err, Vix_GetErrorText(err, NULL));
		return false;
	}
	return true;
}

struct VMRunCheckData
{
	string vmpath;
	bool isRunning;
};

void VixVmRunningCallback(VixHandle jobHandle, VixEventType eventType, VixHandle moreEventInfo, void *clientData)
{
	VixError err = VIX_OK;
	char *url = NULL;

	// Check callback event; ignore progress reports.
	if (VIX_EVENTTYPE_FIND_ITEM != eventType || clientData == NULL) {
		return;
	}

	// Found a virtual machine.
	err = Vix_GetProperties(moreEventInfo, VIX_PROPERTY_FOUND_ITEM_LOCATION, &url, VIX_PROPERTY_NONE);
	if (!VIX_FAILED(err)) 
	{
		VMRunCheckData * pdata = (VMRunCheckData *)clientData;
		if (pdata->vmpath.compare(url) == 0)
			pdata->isRunning = true;
	}
	Vix_FreeBuffer(url);
}

bool CommandRunner::IsVMRunning(VixHandle hostHandle)
{
	VMRunCheckData data;
	data.isRunning = false;
	data.vmpath = this->vmFileName;

	VixHandle jobHandle = VixHost_FindItems(hostHandle, VIX_FIND_RUNNING_VMS, VIX_INVALID_HANDLE, -1, VixVmRunningCallback, (void *)&data);
	VixError err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);
	if (VIX_FAILED(err)) {
		fprintf(stderr, "Find items failed (%"FMT64"d %s)\n", err, Vix_GetErrorText(err, NULL));
		return data.isRunning;
	}
	return data.isRunning;
}

bool CommandRunner::powerON(VixHandle vmHandle)
{
	VixHandle jobHandle = VixVM_PowerOn(vmHandle, VIX_VMPOWEROP_NORMAL, VIX_INVALID_HANDLE, NULL, NULL);
	VixError err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);
	if (VIX_FAILED(err)) {
		fprintf(stderr, "Power On Failed (%"FMT64"d %s)\n", err, Vix_GetErrorText(err, NULL));
		return false;
	}
	return true;
}

bool CommandRunner::powerOFF(VixHandle vmHandle)
{
	VixHandle jobHandle = VixVM_PowerOff(vmHandle, VIX_VMPOWEROP_FROM_GUEST, NULL, NULL);
	VixError err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);
	if (VIX_FAILED(err)) {
		fprintf(stderr, "Power off Failed (%"FMT64"d %s)\n", err, Vix_GetErrorText(err, NULL));
		return false;
	}
	return true;

}

bool CommandRunner::loginVM(VixHandle vmHandle)
{
	int option = fInteractive ? VIX_LOGIN_IN_GUEST_REQUIRE_INTERACTIVE_ENVIRONMENT : 0;
	VixHandle jobHandle = VixVM_LoginInGuest(vmHandle, vmLoginName.c_str(), vmPassword.c_str(), option, NULL, NULL);
	VixError err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);
	Vix_ReleaseHandle(jobHandle);
	if (VIX_FAILED(err)) {
		fprintf(stderr, "failed to login to vm (%"FMT64"d %s)\n", err, Vix_GetErrorText(err, NULL));
		return false;
	}
	return true;
}

bool CommandRunner::EnableUSb(VixHandle vmHandle)
{
	VixHandle jobHandle = VixVM_WriteVariable(vmHandle, VIX_VM_CONFIG_RUNTIME_ONLY, "usb.present", "true", 0, NULL, NULL);
	VixError err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);
	Vix_ReleaseHandle(jobHandle);
	if (VIX_FAILED(err)) {
		fprintf(stderr, "failed enable usb (%"FMT64"d %s)\n", err, Vix_GetErrorText(err, NULL));
		return false;
	}
	return true;
}

bool CommandRunner::DisableUSb(VixHandle vmHandle)
{
	VixHandle jobHandle = VixVM_WriteVariable(vmHandle, VIX_VM_CONFIG_RUNTIME_ONLY, "usb.present", "false", 0, NULL, NULL);
	VixError err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);
	Vix_ReleaseHandle(jobHandle);
	if (VIX_FAILED(err)) {
		fprintf(stderr, "failed disable usb (%"FMT64"d %s)\n", err, Vix_GetErrorText(err, NULL));
		return false;
	}
	return true;
}

bool CommandRunner::runCommand(function<bool(VixHandle)> cmd)
{
	VixHandle hostHandle = VIX_INVALID_HANDLE;
	VixHandle vmHandle = VIX_INVALID_HANDLE;
	bool success = false;
	bool fOnVM = false;

	// open vix connect
	if (connectVIX(&hostHandle))
	{
		// open the vm
		if (openVM(hostHandle, &vmHandle))
		{
			// check if vm is running
			if (!IsVMRunning(hostHandle))
			{
				if (fRuningVMOnly)
				{
					fprintf(stderr, "VM is not running \n");
					goto Error;
				}
				fOnVM = powerON(vmHandle);
			}
			// log into the guest
			if (loginVM(vmHandle))
			{
				// run the command
				success = cmd(vmHandle);
				VixVM_LogoutFromGuest(vmHandle, NULL, NULL);
			}
		}
	}

Error:
	if (vmHandle != VIX_INVALID_HANDLE)
	{
		Vix_ReleaseHandle(vmHandle);
	}

	if (fOnVM)
	{
		powerOFF(vmHandle);
	}

	if (hostHandle != VIX_INVALID_HANDLE)
	{
		VixHost_Disconnect(hostHandle);
		Vix_ReleaseHandle(hostHandle);
	}
	return success;
};

bool CommandRunner::ChangeUSB(bool fEnable)
{
	VixHandle hostHandle = VIX_INVALID_HANDLE;
	VixHandle vmHandle = VIX_INVALID_HANDLE;
	bool success = false;

	// open vix connect
	if (connectVIX(&hostHandle))
	{
		// open the vm
		if (openVM(hostHandle, &vmHandle))
		{
			// check if vm is running
/*			if (!IsVMRunning(hostHandle))
			{
				fprintf(stderr, "VM is not running. \n");
			}
			else
			*/{
				if (fEnable)
					success = EnableUSb(vmHandle);
				else
					success = DisableUSb(vmHandle);
			}

		}
	}

	if (vmHandle != VIX_INVALID_HANDLE)
	{
		Vix_ReleaseHandle(vmHandle);
	}

	if (hostHandle != VIX_INVALID_HANDLE)
	{
		VixHost_Disconnect(hostHandle);
		Vix_ReleaseHandle(hostHandle);
	}
	return success;
};
