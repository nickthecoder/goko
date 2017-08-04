GoKo
====

GoKo is a cross-platform Go board.

You can play Go against the computer (using GNU's go AI), play a local two player game, review and edit games and hone your go skills by solving go problems.
Go problems, and a Joseki dictionary can be downloaded from "Preferences" section of GoKo.

It includes some features not found in a typical Go program, such as game variations, such as "Hidden Move Go", and "One Colour Go".

It is written in Kotlin, which means it can run on any operating system supporting Java.


Home Page
---------

For more information, visit the GoKo home page :

http://nickthecoder.co.uk/wiki/view/software/Kogo

Build
-----

As GoKo is still a work-in-progress, there are no pre-made binaries, so you will need to compile from the source code.
Before you start, you'll need java (including javafx), and gradle installed on your system.

For Debian Linux (as root) :

    apt-get install openjdk-8-jdk libopenjfx-java gradle

Note. If you are running an old version of Debian (e.g. Jessie), you will need to download gradle v2+ manually
because Jessie only supports gradle version 1.5, and I believe version 2.0+ is required.

GoKo uses some components from another of my projects "paratask", so you will need to download and compile that first :

    git clone https://github.com/nickthecoder/paratask.git
    cd paratask
    gradle install

Now download and compile goko :

    git clone https://github.com/nickthecoder/goko.git
    cd goko
    gradle installApp

Run
---

