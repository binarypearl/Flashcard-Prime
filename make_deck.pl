#!/usr/bin/perl

use strict;

use Getopt::Long;

my $version = "0.01";

my $deck_name = "";
my $input_file = "";
my $version_flag = "";

my $front_card_text = "";
my $back_card_text = "";

my $file_name_front = "";
my $file_name_back = "";

my @records = ();
my $record = "";

my $project_dir = "/mnt/projects/java/genetics_era/Flashcard_Prime/assets/decks";

GetOptions (
	"deckname=s" => \$deck_name,
	"inputfile=s" => \$input_file,
	"version" => \$version_flag,
);

chomp ($deck_name);

if ($version_flag) {
	print "make_deck.pl version: $version\n";
	exit (0);
}

if ($deck_name eq "" || $input_file eq "") {
	print "usage: make_deck.pl --deckname \"name of deck\" --inputfile \"path to input\"\n";
}

# lets make a directory for the requested deck:
system ("mkdir -p $project_dir/$deck_name");

# Now lets parse though the input file:
open (INPUTFILE, "$input_file");

@records = <INPUTFILE>;

foreach $record (@records) {
	chomp $record;

	if ($record =~ m/(.*)(:)(.*)/) {
		$front_card_text = $1;
		$back_card_text = $3;

		# need:
		# 1.  generate file names as strings here.
		# 2.  Add front_file_name and back_file_name to gimp script below

		# yes, $file_name_back is being set on $front_card_text on purpose.
		$file_name_front = $front_card_text . "_a.png";		
		$file_name_back = $front_card_text . "_b.png";		

		$file_name_front =~ s/^ /sp/;
		$file_name_back =~ s/^ /sp/;

		$file_name_front =~ s/ /_/g;
		$file_name_back =~ s/ /_/g;

		$file_name_front =~ s/\+/plus/g;
		$file_name_back =~ s/\+/plus/g;

		$file_name_front =~ s/-/minus/g;
		$file_name_back =~ s/-/minus/g;

		$file_name_front =~ s/\*/times/g;
		$file_name_back =~ s/\*/times/g;

		$file_name_front =~ s/\//divideby/g;
		$file_name_back =~ s/\//divideby/g;

		$file_name_front =~ s/\?/what/g;
		$file_name_back =~ s/\?/what/g;

		$file_name_front =~ s/=/equals/g;
		$file_name_back =~ s/=/equals/g;

		$file_name_front =~ s/\\n/_/g;
		$file_name_back =~ s/\\n/_/g;

		$file_name_front =~ s/\(/_/g;
		$file_name_back =~ s/\(/_/g;

		$file_name_front =~ s/\)/_/g;
		$file_name_back =~ s/\)/_/g;

		$file_name_front =~ s/>/_/g;
		$file_name_back =~ s/>/_/g;

		$file_name_front =~ s/</_/g;
		$file_name_back =~ s/</_/g;

		`gimp -i -b '(script-fu-create-flashcard "$front_card_text" "$back_card_text" "$project_dir/$deck_name/$file_name_front" "$project_dir/$deck_name/$file_name_back")' -b '(gimp-quit 0)'`;
	}
}
