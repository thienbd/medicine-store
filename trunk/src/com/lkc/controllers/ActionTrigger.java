package com.lkc.controllers;

import java.io.Serializable;

public interface ActionTrigger extends Serializable{
	public void doAction() throws Throwable;
}
