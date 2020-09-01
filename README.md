# PaymentInitiationSandbox
Application for validating the incoming requests from TPP

Main-class - PaymentIntiationSandboxAppApplication
Run the main class as java application after the maven build.

RestEndpoint URL : http://localhost:8080/v1.0.0/initiate-payment

Input Json :
{"debtorIBAN":"NL02RABO7134384551",
"creditorIBAN":"NL94ABNA1008270121",
"amount":"1.00"}

Headers
X-Request-Id
Signature-Certificate
