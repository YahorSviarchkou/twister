; Twister Installer
[Setup]
AppName=Twister
AppVersion=1.0.0
DefaultDirName={userpf}\Twister
DefaultGroupName=Twister
OutputDir=build\installer
OutputBaseFilename=TwisterInstaller
Compression=lzma
SolidCompression=yes

[Files]
Source: "build\app-image\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Twister"; Filename: "{app}\bin\Twister.exe"

[UninstallDelete]
Type: filesandordirs; Name: "{app}"
