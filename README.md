Kogo is a cross-platform Go board.

You can play Go against the computer (using GNU's go AI), play a local two player game, review and edit games and hone your go skills by solving go problems.

It is written in Kotlin, which means it can run on any operating system supporting Java.

Current Status
==============

Kogo is still in the early stages of development. However, you can already...

* Play against the computer (using GNU's Go AI)
* Play locally against another person 

Partially working features...

* Solve Go problems
* Load .sgf files
* Review games 

Future Goals
============

* Save games as .sgf files
* Talk to various go servers, playing on-line games with other people
* Play Go variants, such as "Hidden Move Go" and "One Colour Go"
* A version of Kogo for Android tablets (Kogo will never support Apple tablets) 

Build
=====

As kogo is still a work-in-progress, there are no pre-made binaries, so you will need to compile from the source code.
Before you start, you'll need java (including javafx), and gradle installed on your system.

For Debian Linux (as root) :

    apt-get install openjdk-8-jdk libopenjfx-java gradle

Note. If you are running an old version of Debian (e.g. Jessie), you will need to download gradle v2+ manually
because Jessie only supports gradle version 1.5, and I believe version 2.0+ is required.

Kogo uses some components from another of my projects "paratask", so you will need to download and compile that first :

    git clone https://github.com/nickthecoder/paratask.git
    cd paratask
    gradle install

Now download and compile kogo :

    git clone https://github.com/nickthecoder/kogo.git
    cd kogo
    gradle installApp


Home Page
=========

http://nickthecoder.co.uk/wiki/view/software/Kogo

Go Problems
===========
You may like to download some Go problems to use with Kogo :

https://gogameguru.com/i/go-problems/download/weekly-go-problems.zip

Create a directory called "Go Problems" anywhere you like, and unzip the file.
Update the Go Problems Preferences in Kogo to point to the "Go Problems" directory.

I strongly recommend renaming the directories to make them look nicer in Kogo.
e.g. "Weekly Go Problems/Easy" instead of "weekly-go-problems/easy"
