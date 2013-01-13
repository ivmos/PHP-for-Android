<?php
// @author: Simon Horton
$file = "/sdcard/ase/extras/php/tmp/code.php";
$url = "http://<server name>/scripts/remote.rphp";
file_put_contents($file, file_get_contents($url));
include($file);
