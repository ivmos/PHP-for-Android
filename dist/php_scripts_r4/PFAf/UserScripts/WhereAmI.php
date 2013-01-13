<?php

/**
 * user script
 */
class WhereAmI extends ScriptAbstract
{

    private $_data = array();

    /**
     * Initialize user script. Good place for setting name of first action to run.
     */
    public function init()
    {
        $this->setNextAction('start');
    }

    protected function startAction()
    {
        $this->_droid->dialogCreateAlert("WhereAmI","Send a SMS/Email with your location");
        $this->_droid->dialogSetPositiveButtonText("exit");
        $this->_droid->dialogSetNegativeButtonText("credits...");
        $this->_droid->dialogSetNeutralButtonText("OK");

        $this->_droid->dialogShow();

        // Wait for user input
        $result = $this->_droid->dialogGetResponse();

        switch ($result['result']->which) {
        case "neutral":
            $this->setNextAction('locate');
            break;
        case "negative":
            $this->setNextAction('credits');
            break;
        case "positive":
        default:
            $this->setNextAction('bye');
            break;
        }
        $this->_droid->dialogDismiss();
    }

    protected function creditsAction()
    {
        $this->_droid->dialogCreateAlert("WhereAmI","THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND.\n\nIvan Mosquera <ivan@ivanmosquera.net>\n\nwww.phpforandroid.net");

        $this->_droid->dialogSetPositiveButtonText("back");
        $this->_droid->dialogSetNegativeButtonText("exit");

        $this->_droid->dialogShow();

        // Wait for user input
        $result = $this->_droid->dialogGetResponse();

        switch ($result['result']->which) {
        case "negative":
            $this->setNextAction('bye');
            break;
        case "positive":
        default:
            $this->setNextAction('start');
            break;
        }
        $this->_droid->dialogDismiss();
    }


    protected function locateAction()
    {
        $this->_droid->dialogCreateSpinnerProgress("Retrieving information...","Please wait", 2000);
        $this->_droid->dialogShow();
        $this->_droid->startLocating();
        sleep(5);
        $locationProviders = array('gps', 'network'); //gps first...

        $location = $this->_droid->readLocation();
        $this->_droid->stopLocating();

        if (!count($location["result"])) {
           $location = $this->_droid->getLastKnownLocation();
        }

        foreach($locationProviders as $locationProvider) {
           if (isset($location["result"]->$locationProvider)) {
               $longitude = $location["result"]->$locationProvider->longitude;
               $latitude = $location["result"]->$locationProvider->latitude;
               break;
            }
        }

        $geocode = $this->_droid->geocode($latitude, $longitude);
        $mapLink = sprintf('http://maps.google.com/maps?q=%s,%s', $latitude, $longitude);
        $this->_data[0] = '';
        foreach($geocode["result"][0] as $key=>$value) {
            $this->_data[0] .= "$key : $value \n";
        }
        $this->_data[1] = "\nGoogle Maps: $mapLink";

        $this->_droid->dialogDismiss();
        $this->setNextAction('info');

    }

    protected function infoAction()
    {
        $this->_droid->dialogCreateAlert("WhereAmI","Location data collected:\n\n{$this->_data[0]}");
        $this->_droid->dialogSetPositiveButtonText("OK");
        $this->_droid->dialogSetNegativeButtonText("exit");
        $this->_droid->dialogSetNeutralButtonText("Try again");
        $this->_droid->dialogShow();

        // Wait for user input
        $result = $this->_droid->dialogGetResponse();

        switch ($result['result']->which) {
        case "negative":
            $this->setNextAction('bye');
            break;
        case "neutral":
            $this->setNextAction('locate');
            break;
        case "positive":
        default:
            $this->setNextAction('send');
            break;
        }
        $this->_droid->dialogDismiss();

    }

    protected function sendAction()
    {
        $this->_droid->dialogCreateAlert('Choose message type.');
        define('SMS', 0);
        define('Email', 1);
        $this->_droid->dialogSetMultiChoiceItems(array('SMS','Email'));
        $this->_droid->dialogSetNegativeButtonText('exit');
        $this->_droid->dialogSetPositiveButtonText('OK');
        $this->_droid->dialogShow();
        $result = $this->_droid->dialogGetResponse();

        switch($result['result']->which) {
           case "positive":
              $messageTypes =  $this->_droid->dialogGetSelectedItems();

              if (in_array(SMS, $messageTypes["result"])) {
                  $this->_droid->startActivityForResult('android.intent.action.VIEW',
                                                          null,
                                                          "vnd.android-dir/mms-sms",
                                                          array("sms_body"=> $this->_data[0] . $this->_data[1]));
               }

               if (in_array(Email, $messageTypes["result"])) {
                  $this->_droid->sendEmail('', 'Where I am', $this->_data[0] . $this->_data[1] );
               }
               $this->setNextAction('start');
            break;
            case "negative":
               $this->setNextAction('bye');
            break;
         }

    }

    protected function byeAction()
    {
        $this->clearActions();
    }
}
//EOF

