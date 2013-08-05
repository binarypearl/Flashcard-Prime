Eventually want to get this file included in the .apk, in the about section.

-----------------------------------------------------------------------------------
Credit goes to Neil Davies for the 3d-flip feature:
http://www.inter-fuser.com/2009/08/android-animations-3d-flip.html
-----------------------------------------------------------------------------------

-----------------------------------------------------------------------------------
Terms:
  Deck ->       A deck is a directory with X number of pair's of .png files.
                The name of the deck (directory) is very important.  We use this 
                format:  category_specific

                The reason that this is important, is that I use the name of this 
                directory to determine what info to put into the database, as well
                as the names of what to display on the FlashCardMainActivity page.
 
                Basically I divide decks into 2 concepts: 'category' and 'specific'.
                The 'category' is something like 'Math' or 'Spanish' or 'Linux'.
                The specific is the more detailed description of what the cards
                are about.  The goal with this setup is to eventually make it
                so you can see/get all decks for Math for example.  While that's
                eventual, the current practical use of this is just to display the
                category and specific on the FlashCardMainActivity page.

  FlashCard ->  A FlashCard is made up of 2 .png files.  The 'front' of the flashcard
                is something_a.png and the 'back' of the flashcard is something_b.png.
                So the difference is the '_a' and '_b' in the name.  'something' is 
                always the same, whatever it is, for any given pair of .png files.
 
------------------------------------------------------------------------------------

------------------------------------------------------------------------------------
Source code atlas:
  There is a lot of components that make up Flashcard Prime.  This 
  info below gives an outline of what each file does.  Typical android files
  that are found in any project are not described here.  Instead this is 
  mean to show you all of the non-typical stuff in this project.

  root directory of everything Flashcard Prime:
  FlashCard/

  FlashCard/$category_$specific.txt
   These are the input files used to create various decks.  They really don't
   need to be here, and actually kind of generate clutter.  A seperate 
   directory may be beneficial.  These are only used on creation of a deck,
   but I want to keep them around for historical reasons or to re-generate a 
   deck if needed.  These files are pure text.  The format for these files is:
   $front_text:$back_text.  So everything you type on the left side of the ':'
   goes on the front .png, and everything right of the ':' goes on the back 
   .png.

  FlashCard/make_deck.pl
   This is wrapper script to make a deck of flashcards.  The actual script 
   that generates just 1 pair of .png's is a gimp script-fu script located here:
   $HOME/.gimp-2.6/scripts/create_flashcards.scm.  This script is described in 
   detail below, but the make_deck.pl calls this gimp script-fu script foreach
   flashcard that you want to create.  

   make_deck.pl takes in two parameters (-d deck_name and -i input_file.txt)
   -d deck_name.txt .   
  
  $HOME/.gimp-2.6/scripts/create_flaschards.scm
   My first and perhaps hopefully last Scheme script ;)  This script is the nuts
   and bolts of creating the .png files.  As arguments, this script takes in:
   "front text" "back text" "filename of frontcard" "filename of backcard".
   The mechanics of this script are described in the script itself.  This 
   is a very tricky script, so the preference is to keep this script as
   minimal as possible, so it only creates a pair of .png's. 
   make_deck.pl is easier to use to creating mass amount of flashcards,
   and make_deck.pl uses create_flaschards.scm in a loop. 

   It's actually `gimp` that uses this script.  The gimp's script-fu 
   scripting engine is what reads in these commands.  Unfortunately the only  
   thing is that I had to place this script in $HOME/.gimp-2.6/scripts/.
   Ideally it would have been included in the Flashcard/* somewhere, but
   I don't know of a way to make it work like that.

  FlashCard/README.txt
   This File that you are reading.  

  FlashCard/yay_for_build.py
   Run this script to create a new debug build.  It does require an android
   device to be attached, otherwise it will hang after the build is complete.
    

  FlashCard/assets/
   assets are an Android concept.  This is where you put any type of file that 
   you want included in the application itself (ex pictures, mp3's)

  FlashCard/assets/decks/
   This is where we store the decks.  Each folder with Flashcard/assets/decks/*
   contains the actual .png files that will be displayed.

  FlashCard/assets/flashcard_flip/
   While not very well named, this contains just the images for the home screen 
   tutorial flashcard(s).

  FlashCard/assets/icons/
   As of right now, this just contains the 512x512 image of the application icon.
   This eventually is required to put on google play so I have read.

  FlashCard/assets/songs/
   This is where are placing all of the mp3's of songs to play within the 
   application.


  FlashCard/res/
   This is an Android construct.  These contains xml files that are read
   into the overall running applications from the .java files.

  FlashCard/res/layout/
   These are the base layout xml files for each Activity.  I'm going against  
   the grain here, and using xml very minimally in favor of adding widgets
   and whatnot in the .java files.  There is currently 1 .xml file for each
   corresponding Activity.

  FlashCard/res/menu/
   We only have one xml file here, main_menu.xml.  This is used in 
   FlashCardMainActivity.java to ultimately create the menu of decks to
   choose from.

  FlashCard/res/values/
   2 files here:
   1.  strings.xml -> standard android file to include string constants in.
   2.  arrays.xml -> This is used for setting the background color.  This
                     file may eventually be depricated or we fix the code
                     behind this, which currently doesn't work since
                     we started creating our own layout's in code.

  FlashCard/res/xml/
   This is part of the preference screen.  While not currently being used,
   we will eventually bring this back into use.


  FlashCard/src/com/flashcard/*
   These are all of the .java files that make up Flashcard Prime.

   HomeScreenActivity.java
    This is the first screen that is launched.  This is where the basic
    tutorial is and the button to proceed to the next decks.

   FlashCardMainActivity.java
    Here is the screen that displays which decks that you have avaiable.

   FlashCardDisplayActivity.java
    This is the class that displays the decks.  It's a common routine that
    gets the list of cards from the database and displays them.
  
   MainMenuPrefsActivity.java
    Small class to display the Preferences Screen when selected.

   DBHelper.java
    Initial database creation.  We may eventually depricate this, or 
    expand it, depending on which makes more sense.

   SwapViews.java, DisplayNextView.java and Flip3dAnimation.java
    These 3 files were acquired from another site that is documented above
    in this README.txt file.  These are all part of what allows us to 
    display and rotate the flashcards.
   

------------------------------------------------------------------------------------
