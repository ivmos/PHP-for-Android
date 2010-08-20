/*
 * Copyright (C) 2010 Irontec SL
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

package com.irontec.phpforandroid;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.googlecode.android_scripting.Log;
import com.googlecode.android_scripting.interpreter.PfaHostedInterpreter;
import com.googlecode.android_scripting.interpreter.Sl4aHostedInterpreter;

/**
 * 
 * @author Ivan Mosquera Paulo (ivan@irontec.com)
 *
 */
public class PhpDescriptor extends PfaHostedInterpreter {
  public static final String PHP_BASE_INSTALL_URL = "http://php-for-android.googlecode.com/files/";
  private static final String PHP_BIN = "bin/php";
  private final static String PHP_PREFIX =
    "-c /sdcard/com.irontec.phpforandroid/extras/php/php.ini";
  //"%s";

  public String getExtension() {
    return ".php";
  }

  public String getName() {
    return "php";
  }

  public String getNiceName() {
    return "PHP 5.3.3"; 
  }

  public boolean hasInterpreterArchive() {
    return true;
  }

  public boolean hasExtrasArchive() {
    return true;
  }

  public boolean hasScriptsArchive() {
    return true;
  }

  public int getVersion() {
    return 2;
  }
  
  @Override
  public int getExtrasVersion() {
    return 2;
  }

  @Override
  public int getScriptsVersion() {
    return 2;
  }

  public String getBinary() {
    return PHP_BIN;
  }

  public String getEmptyParams(Context context) {
    return "";
  }

//  public String getExecuteParams(Context context) {
//    //return " -c " + getPath(context) + " %s";
//    //return String.format(format, args)" -c /sdcard/com.irontec.phpforandroid/extras/php ";
//    //return PHP_PREFIX;///, getPath(context), getBinary());//, "load('%s')");
//    return " -c /sdcard/com.irontec.phpforandroid/extras/php %s";
//  }
    
  
//  public String[] getExecuteArgs(Context context) {
//    String args[] = { "-c /sdcard/com.irontec.phpforandroid/extras/php %s"};
//    //return " -c " + getPath(context) + " %s";
//    //return " -c /sdcard/com.irontec.phpforandroid/extras/php %s";
//    return args;
//  }

//  public String getExecuteCommand(Context context) {
//    return String.format("%1$s/%2$s", getPath(context), getBinary());
//  }
  
//  @Override
//  public String getScriptCommand(Context context) {
//    //String absolutePathToJar = new File(getExtrasPath(context), JRUBY_JAR).getAbsolutePath();
//    //return String.format(JRUBY_PREFIX, absolutePathToJar, "load('%s')");
//    return /*getBinary(context) +*/ "-c /sdcard/com.irontec.phpforandroid/extras/php/php.ini %s";
//  }
  
  @Override
  public List<String> getArguments(Context context) {
    //String absolutePathToJar = new File(getExtrasPath(context), JRUBY_JAR).getAbsolutePath();
    return Arrays.asList("-c", "/sdcard/com.irontec.phpforandroid/extras/php/php.ini");
  }
  
  @Override
  public String getExtrasArchiveUrl() {
    return PHP_BASE_INSTALL_URL + getExtrasArchiveName();
  }


  @Override
  public String getInterpreterArchiveUrl() {
    return PHP_BASE_INSTALL_URL + getInterpreterArchiveName();
  }

  @Override
  public String getScriptsArchiveUrl() {
    return PHP_BASE_INSTALL_URL + getScriptsArchiveName();
  }
  
  @Override
  public String getInteractiveCommand(Context context) {
    return /*getBinary(context) +*/ " -a";
  }
  
  @Override
  public File getBinary(Context context) {
    return new File(getExtrasPath(context), PHP_BIN);
  }



 
  
  
  
  
  
  

}
