<?php
class Application
{
        /**
         * User script name to use
         * @var string
         */
        protected $_scriptName = '';

        /**
         * @param array $config possible options are:
         *              - scriptName (string) Name of the script to load. Class name must be same as script name
         */
        public function __construct(array $config = array())
        {
                $this->_scriptName = isset($config['scriptName']) ? $config['scriptName'] : '';
        }

        public function run()
        {
                require_once 'Android.php';
                $droid = new Android();

                // load and initialize user script
                require_once realpath(dirname(__FILE__) . '/../UserScripts') . '/' . $this->_scriptName . '.php';
                $userScript = new $this->_scriptName($droid);
                $userScript->init();

                // loop through every action
                do {
                        // run current action
                        $userScript->action();
                } while($userScript->isActionStackEmpty());
                $droid->exit();
        }
}
