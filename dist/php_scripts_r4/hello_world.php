<?php
require_once("Android.php");
$droid = new Android();
$name = $droid->getInput("Hi!", "What is your name?");
$droid->makeToast('Hello, ' . $name['result']);
