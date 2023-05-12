/*
 * Copyright 2023 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
message_payload = com.epam.digital.data.platform.el.juel.MessagePayloadJuelFunction.&message_payload
process_caller = com.epam.digital.data.platform.el.juel.ProcessCallerJuelFunction.&process_caller
save_digital_document_from_url = com.epam.digital.data.platform.el.juel.SaveDigitalDocumentFromUrlJuelFunction.&save_digital_document_from_url
save_digital_document = com.epam.digital.data.platform.el.juel.SaveDigitalDocumentJuelFunction.&save_digital_document
get_trembita_auth_token = com.epam.digital.data.platform.el.juel.GetTrembitaAuthTokenJuelFunction.&get_trembita_auth_token
load_digital_document = com.epam.digital.data.platform.el.juel.LoadDigitalDocumentJuelFunction.&load_digital_document