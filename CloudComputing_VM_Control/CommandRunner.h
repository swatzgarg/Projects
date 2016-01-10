#pragma once

#include <string>
#include <functional>
#include "vix.h"
using namespace std;

/*
Author : Swati Garg
*/
class CommandRunner
{
private:
	string vmFileName;  // path to the vmx file
	string vmLoginName; // login name 
	string vmPassword;  // password for the above login name
	bool fRuningVMOnly; // run command only if vm is running
	bool fInteractive;  // run command in interactive mode.
		
public:
	CommandRunner(string vmFileName, string vmLoginName, string vmPassword, bool fRuningVMOnly,	bool fInteractive);
	~CommandRunner();

	bool runCommand(function<bool(VixHandle)> cmd);
	bool ChangeUSB(bool fEnable);
private:
	bool connectVIX(VixHandle* phostHandle);
	bool openVM(VixHandle hostHandle, VixHandle* pvmHandle);
	bool loginVM(VixHandle vmHandle);
	bool IsVMRunning(VixHandle hostHandle);
	bool powerON(VixHandle vmHandle);
	bool powerOFF(VixHandle vmHandle);
	bool EnableUSb(VixHandle vmHandle);
	bool DisableUSb(VixHandle vmHandle);
};

