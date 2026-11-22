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
WizardStyle=modern

[Tasks]
Name: "startmenu"; Description: "Create Start Menu shortcut"; GroupDescription: "Additional icons:"; Flags: unchecked
Name: "desktopicon"; Description: "Create Desktop shortcut"; GroupDescription: "Additional icons:"; Flags: unchecked

[Files]
Source: "build\app-image\Twister\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Twister"; Filename: "{app}\Twister.exe"; Tasks: startmenu
Name: "{userdesktop}\Twister"; Filename: "{app}\Twister.exe"; Tasks: desktopicon

[UninstallDelete]
Type: filesandordirs; Name: "{app}"