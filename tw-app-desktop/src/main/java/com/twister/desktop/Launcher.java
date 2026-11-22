package com.twister.desktop;

import javafx.application.Application;

/** Must not extend {@link Application}: the JVM refuses to start such a main class from the classpath. */
// TODO(discuss): JavaFX and SQLite unpack DLLs into the profile, and System.load fails for a path outside the ANSI
// code page (a Georgian user name on Russian Windows): the app does not start. Ship the DLLs with the installer?
// TODO(discuss): nothing stops a second copy of the app on the same data folder, while ADR 0009 assumes one process
// for the database, and the log and the window state are not made for two writers. Add a single-instance lock?
// TODO(discuss): a failed start only reaches the log and the console, and an installed app has no console: the user
// sees no window at all. Show a dialog that says where the log is?
public final class Launcher {

    private Launcher() {}

    public static void main(String[] args) {
        Application.launch(TwisterFx.class, args);
    }
}
