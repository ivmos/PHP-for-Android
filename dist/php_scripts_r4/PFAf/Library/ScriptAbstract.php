<?php
/**
 * This class is intended to be parent class to all user scripts.
 */
abstract class ScriptAbstract
{
        /**
         * List of actions to execute
         * @var array
         */
        protected $_actionStack = array();

        /**
         * @var Android
         */
        protected $_droid = null;

        public function __construct(Android $droid)
        {
                $this->_droid = $droid;
        }

        public function action()
        {
                // get next action name
                $nextAction = $this->getNextAction() . 'Action';

                // check if this action exists
                if(!is_callable(array($this, $nextAction))) {
                        throw new Exception('There is no ' . $nextAction . ' in your script.');
                }

                // run action defined in user script
                $this->$nextAction();
        }

        /**
         * Checks if there are some action tu run
         * @return bool
         */
        public function isActionStackEmpty()
        {
                return count($this->_actionStack) > 0;
        }

        /**
         * This method adds new action to stack. Action is added as first to run.
         * Adding more actions one by one, cause that stack will act as FILO (first in last out)
         * @param string $name
         */
        public function setNextAction($name)
        {
                array_unshift($this->_actionStack, $name);
        }

        /**
         * Gets name of the next action to run
         */
        public function getNextAction()
        {
                return array_shift($this->_actionStack);
        }

        /**
         * Adds new action to run to the end of stack.
         * Adding more actions one by one cause that stack will act as FIFO (first in first out)
         * @param string $name
         */
        public function addAction($name)
        {
                array_push($this->_actionStack, $name);
        }

        /**
         * Removes all actions from stack
         */
        public function clearActions()
        {
                $this->_actionStack = array();
        }

        abstract public function init();
}
