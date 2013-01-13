<?php
// horoscope.php: Proof of Concept retrieving horoscope information
// @author: Javier Infante <jabiAT_NOSP_AMirontec.com>

require_once("Android.php");
$droid = new Android();

$action = "start";
while (true) {

   switch($action) {
      case "start":
         $droid->dialogCreateAlert("PFA-Horoscope", "Ask PFAewok for your Horoscope");

         $droid->dialogSetPositiveButtonText("ask now!");
         $droid->dialogSetNegativeButtonText("credits...");

         $droid->dialogShow();

         // Wait for user input
         $result = $droid->dialogGetResponse();

         switch ($result['result']->which) {
            case "negative":
               $action = "credits";
                break;
            case "positive":
            default:
               $action = "choose_and_ask";
                break;
         }
         $droid->dialogDismiss();
          break;

      case "credits":
         $droid->dialogCreateAlert(
             "PFA-Horoscope",
             "Only for educational Purpouses (what else?).\n\nHoroscope Source: http://widgets.fabulously40.com/horoscope.json\n\nwww.phpforandroid.net"
         );

         $droid->dialogSetPositiveButtonText("back");
         $droid->dialogSetNegativeButtonText("exit");

         $droid->dialogShow();

         // Wait for user input
         $result = $droid->dialogGetResponse();

         switch ($result['result']->which) {
         case "negative":
            $action = "bye";
             break;
         case "positive":
         default:
            $action = "start";
             break;
         }
         $droid->dialogDismiss();
          break;

      case "choose_and_ask":
         $droid->dialogCreateAlert("Choose your sign:");
         $zodiac = array("aries", "taurus", "gemini", "cancer", "leo", "virgo", "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces");
         $droid->dialogSetItems($zodiac);
         $droid->dialogShow();

         $result = $droid->dialogGetResponse();

         $url = "http://widgets.fabulously40.com/horoscope.json?sign=".$zodiac[$result['result']->item];

         // Downloading the horoscope might take a while...
         $droid->dialogCreateSpinnerProgress("Retrieving information...", "Please wait");
         $droid->dialogShow();

         // Downloading and de-json-ing
         $result = json_decode(file_get_contents($url));

         // we are ready!
         $droid->vibrate();

         // Close spinner
         $droid->dialogDismiss();

         $theFuture = html_entity_decode($result->horoscope->horoscope, ENT_QUOTES, "UTF-8");
         // Something is wrong with &apos;...
         $theFuture = str_replace("&apos;", "'", $theFuture);
         $theFuture .= "\n\n[widgets.fabulously40.com]";

         $droid->dialogCreateAlert("Your Future is here " . $result->horoscope->sign . "!", $theFuture);

         $droid->dialogSetPositiveButtonText("Exit");
         $droid->dialogShow();

         // Wait for user input to continue script
         $droid->dialogGetResponse();
         $action = "bye";
          break;

      case "bye":
         $droid->makeToast("Trust the PFAewok!");
         $droid->exit();
         exit();
          break;
   } // Main switch

} // main while

// EOF
