package io.blue.actor.message

import io.blue.model.odi.runtime.lpi.LoadPlanInstanceRun

case class LoadPlanRunErrorMail(mail: String, connection: String, run: LoadPlanInstanceRun)