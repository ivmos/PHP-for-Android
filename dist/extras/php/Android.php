<?php

// Copyright (C) 2010 Irontec SL
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may not
// use this file except in compliance with the License. You may obtain a copy of
// the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// License for the specific language governing permissions and limitations under
// the License.

// @author : Ivan Mosquera Paulo <ivan@irontec.com>



class Android
{

   private $_socket;
   private $_id;

   public function __construct($addr = null)
   {
      if (is_null($addr)) {
         $addr = getenv('AP_HOST');
      }
      $this->_socket = socket_create(AF_INET, SOCK_STREAM, 6); // bionic doesnt support getprotobyname('tcp')
      socket_connect($this->_socket, gethostbyname($addr), getenv('AP_PORT'));
      $this->_id = 0;
      $this->_authenticate(getenv('AP_HANDSHAKE'));

   }

   public function rpc($method, $args)
   {
      $data = array(
         'id'=>$this->_id,
         'method'=>$method,
         'params'=>$args
      );
      $request = json_encode($data);
      $request .= "\n";
      $sent = socket_write($this->_socket, $request, strlen($request));
      $response = socket_read($this->_socket, 1024, PHP_NORMAL_READ) or die("Could not read input\n");
      $this->_id++;
      $result = json_decode($response);

      $ret =  array ('id' => $result->id,
         'result' => $result->result,
         'error' => $result->error
      );
      return $ret;
   }


   public function __call($name, $args)
   {
      return $this->rpc($name, $args);
   }
}
