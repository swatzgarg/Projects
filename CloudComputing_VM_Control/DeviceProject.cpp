#include "stdafx.h"
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include "CommandRunner.h"
#include <Windows.h>
#include <boost/filesystem.hpp>

extern "C" int _forceCRTManifestCUR = 1;

bool Copy(string vmNameSrc, string filenameSrc, string loginNameSrc, string passwordSrc, string vmNameDest, string filenameDest, string loginNameDest, string passwordDest)
{
	boost::filesystem::path tempfiledir = boost::filesystem::temp_directory_path();
	boost::filesystem::path tempfilename = boost::filesystem::unique_path();
	boost::filesystem::path tempfile = tempfiledir/tempfilename;
	string filename_s = tempfile.string();

	// copy from src to temp location
	CommandRunner * runnerSrc = new CommandRunner(vmNameSrc, loginNameSrc, passwordSrc, false, false);
	if (runnerSrc->runCommand([=](VixHandle vmhandle)->bool {
		VixHandle jobHandle = VixVM_CopyFileFromGuestToHost(vmhandle, filenameSrc.c_str(), filename_s.c_str(), 0, 0, NULL, NULL);
		VixError err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);
		Vix_ReleaseHandle(jobHandle);
		if (VIX_FAILED(err))
		{
			fprintf(stderr, "Copy Failed (%"FMT64"d %s)\n", err, Vix_GetErrorText(err, NULL));
			fprintf(stderr, "Source file is %s\n", filenameSrc.c_str());
			return false;
		}
		return true;
	}))
	{
		CommandRunner * runnerDest = new CommandRunner(vmNameDest, loginNameDest, passwordDest, false, false);
		if (runnerDest->runCommand([=](VixHandle vmhandle)->bool {
			VixHandle jobHandle = VixVM_CopyFileFromHostToGuest(vmhandle, filename_s.c_str(), filenameDest.c_str(), 0, 0, NULL, NULL);
			VixError err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);
			Vix_ReleaseHandle(jobHandle);
			if (VIX_FAILED(err))
			{
				return false;
			}
			return true;
		}))
		{
			boost::filesystem::remove_all(tempfile);;
			return true;
		}
		boost::filesystem::remove_all(tempfile);
	}
	return false;
}

bool TakeScreenshot(string vmNameSrc, string loginNameSrc, string passwordSrc, string filenameDest)
{
	CommandRunner * runnerSrc = new CommandRunner(vmNameSrc, loginNameSrc, passwordSrc, true, true);
	return runnerSrc->runCommand([=](VixHandle vmhandle)->bool {
		VixHandle captureJob = VixVM_CaptureScreenImage(vmhandle, VIX_CAPTURESCREENFORMAT_PNG, VIX_INVALID_HANDLE, NULL, NULL);
		int byte_count;
		byte *screen_bits;
		VixError err = VixJob_Wait(captureJob, VIX_PROPERTY_JOB_RESULT_SCREEN_IMAGE_DATA, &byte_count, &screen_bits, VIX_PROPERTY_NONE);
		Vix_ReleaseHandle(captureJob);
		if (VIX_FAILED(err))
		{
			fprintf(stderr, "failed to capture screen in guest vm '%s'(%"FMT64"d %s)\n", vmNameSrc, err, Vix_GetErrorText(err, NULL));
			return false;
		}

		// write to the file
		FILE *fp = NULL;
		fopen_s(&fp, filenameDest.c_str(), "wb+");
		if (fp)
		{
			fwrite(screen_bits, byte_count, 1, fp);
			fclose(fp);
		}
		// Free blob memory when done.
		Vix_FreeBuffer(screen_bits);
		return true;
	});
}

bool ChangeUSB(string vmNameSrc, string loginNameSrc, string passwordSrc, bool fEnable)
{
	CommandRunner * runnerSrc = new CommandRunner(vmNameSrc, loginNameSrc, passwordSrc, true, true);
	return runnerSrc->ChangeUSB(fEnable);
}

void DoOption1()
{
	string vmNameSrc;
	string vmLoginnameSrc;
	string passwordSrc;
	cout << "Please enter the source machine name : ";
	getline(cin, vmNameSrc);
	cout << "Source Login : ";
	getline(cin, vmLoginnameSrc);
	cout << "Source Password : ";
	getline(cin, passwordSrc);
	ChangeUSB(vmNameSrc, vmLoginnameSrc, passwordSrc,true);
}
void DoOption2()
{
	string vmNameSrc;
	string vmLoginnameSrc;
	string passwordSrc;
	cout << "Please enter the source machine name : ";
	getline(cin, vmNameSrc);
	cout << "Source Login  : ";
	getline(cin, vmLoginnameSrc);
	cout << "Source Password  : ";
	getline(cin, passwordSrc);
	ChangeUSB(vmNameSrc, vmLoginnameSrc, passwordSrc, false);
}
void DoOption3()
{
	string vmNameSrc;
	string srcfilename;
	string vmLoginnameSrc;
	string passwordSrc;
	string vmNameDest;
	string Destfilename;
	string vmLoginnameDest;
	string passwordDest;
	cout << " You Chose a file transfer \n";
	cout << "Please enter the source machine name : ";
	getline(cin, vmNameSrc);
	cout << "Please enter the source file name : ";
	getline(cin, srcfilename);
	cout << "Source Login : ";
	getline(cin, vmLoginnameSrc);
	cout << "Source Password : ";
	getline(cin, passwordSrc);
	cout << "Please enter the destination machine name : ";
	getline(cin, vmNameDest);
	cout << "Please enter the destination file name : ";
	getline(cin, Destfilename);
	cout << "Destination Login : ";
	getline(cin, vmLoginnameDest);
	cout << "Destination Password : ";
	getline(cin, passwordDest);
	Copy(vmNameSrc, srcfilename, vmLoginnameSrc, passwordSrc, vmNameDest, Destfilename, vmLoginnameDest, passwordDest);
}
void DoOption4()
{
	string vmNameSrc;
	string filenameDest;
	string vmLoginnameSrc;
	string passwordSrc;
	cout << "Please enter the source machine name : ";
	getline(cin, vmNameSrc);
	cout << "Please enter the destination file name : ";
	getline(cin, filenameDest);
	cout << "Source Login : ";
	getline(cin, vmLoginnameSrc);
	cout << "Source Password : ";
	getline(cin, passwordSrc);
	TakeScreenshot(vmNameSrc, vmLoginnameSrc, passwordSrc, filenameDest);
}

int _tmain(int argc, _TCHAR* argv[])
{
	if (argc > 1)
	{
		if (strcmp(argv[1], "3") == 0)
			Copy(argv[2], argv[3], argv[4], argv[5], argv[6], argv[7], argv[8], argv[9]);
		if (strcmp(argv[1], "4") == 0)
			TakeScreenshot(argv[2], argv[4], argv[5], argv[3]);
		return 0;
	}

	string choice;
	cout << "welcome";
	cout << "Please choose the operation";
	cout << "Choose: \n  1-> Enable the USB \n 2-> Disable the USB \n 3-> transfer the file \n 4-> Capture screenshot \n 5-> close \n";
	getline(cin, choice);

	if (choice == "1")
	{
		DoOption1();
	}

	if (choice == "2")
	{
		DoOption2();
	}

	if (choice == "3")
	{
		DoOption3();
	}

	if (choice == "4")
	{
		DoOption4();
	}
}
