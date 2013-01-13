<?php
// include required files. will be replaced by spl_autoload and namespaces
require_once 'PFAf/Library/Application.php';
require_once 'PFAf/Library/ScriptAbstract.php';

// create application instance
$app = new Application(array('scriptName' => 'WhereAmI'));
// run application
$app->run();
