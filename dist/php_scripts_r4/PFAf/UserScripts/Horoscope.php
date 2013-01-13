<?php
/**
 * user script
 */
class Horoscope extends ScriptAbstract
{
        /**
         * Initialize user script. Good place for setting name of first action to run.
         */
        public function init()
        {
                $this->setNextAction('start');
        }

        protected function startAction()
        {
                $this->_droid->dialogCreateAlert("PFA-Horoscope","Ask PFAewok for your Horoscope");

                $this->_droid->dialogSetPositiveButtonText("ask now!");
                $this->_droid->dialogSetNegativeButtonText("credits...");

                $this->_droid->dialogShow();

                // Wait for user input
                $result = $this->_droid->dialogGetResponse();

                switch ($result['result']->which) {
                        case "negative":
                                $this->setNextAction('credits');
                                break;
                        case "positive":
                        default:
                                $this->setNextAction('chooseAndAsk');
                                break;
                }
                $this->_droid->dialogDismiss();
        }

        protected function creditsAction()
        {
                $this->_droid->dialogCreateAlert("PFA-Horoscope","Only for educational Purpouses (what else?).\n\nHoroscope Source: http://widgets.fabulously40.com/horoscope.json\n\nwww.phpforandroid.net");

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

        protected function chooseAndAskAction()
        {
                $this->_droid->dialogCreateAlert("Choose your sign:");
                $zodiac = array("aries","taurus","gemini","cancer","leo","virgo","libra","scorpio","sagittarius","capricorn","aquarius","pisces");
                $this->_droid->dialogSetItems($zodiac);
                $this->_droid->dialogShow();

                $result = $this->_droid->dialogGetResponse();

                $url = "http://widgets.fabulously40.com/horoscope.json?sign=".$zodiac[$result['result']->item];

                // Downloading the horoscope might take a while...
                $this->_droid->dialogCreateSpinnerProgress("Retrieving information...","Please wait");
                $this->_droid->dialogShow();

                // Downloading and de-json-ing
                $result = json_decode(file_get_contents($url));

                // we are ready!
                $this->_droid->vibrate();

                // Close spinner
                $this->_droid->dialogDismiss();

                $theFuture = html_entity_decode($result->horoscope->horoscope,ENT_QUOTES,"UTF-8");
                // Something is wrong with &apos;...
                $theFuture = str_replace("&apos;","'",$theFuture);
                $theFuture .= "\n\n[widgets.fabulously40.com]";

                $this->_droid->dialogCreateAlert("Your Future is here ".$result->horoscope->sign . "!",$theFuture);

                $this->_droid->dialogSetPositiveButtonText("Exit");
                $this->_droid->dialogShow();

                // Wait for user input to continue script
                $this->_droid->dialogGetResponse();
                $this->setNextAction('start');
        }

        protected function byeAction()
        {
                $this->clearActions();
        }
}
