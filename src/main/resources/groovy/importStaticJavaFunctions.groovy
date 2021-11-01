/*
 * Bind the static functions to appropriate variables for having ability to call them from script
 * tasks
 */
initiator = com.epam.digital.data.platform.el.juel.InitiatorJuelFunction.&initiator
completer = com.epam.digital.data.platform.el.juel.CompleterJuelFunction.&completer
submission = com.epam.digital.data.platform.el.juel.SubmissionJuelFunction.&submission
sign_submission = com.epam.digital.data.platform.el.juel.SignSubmissionJuelFunction.&sign_submission
system_user = com.epam.digital.data.platform.el.juel.SystemUserJuelFunction.&system_user
get_variable = com.epam.digital.data.platform.el.juel.GetVariableJuelFunction.&get_variable
set_variable = com.epam.digital.data.platform.el.juel.SetVariableJuelFunction.&set_variable
set_transient_variable = com.epam.digital.data.platform.el.juel.SetTransientVariableJuelFunction.&set_transient_variable