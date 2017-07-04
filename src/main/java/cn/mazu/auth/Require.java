/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.*/
 
package cn.mazu.auth;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.lang.ref.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;
import javax.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mazu.utils.exception.WException;

public class Require extends WException {
	private static Logger logger = LoggerFactory.getLogger(Require.class);

	public Require(String method) {
		super("You need to specialize " + method);
	}

	public Require(String method, final String function) {
		super("You need to specialize " + method + " for " + function);
	}
}
