## ITC Client

This is a minimal HTTP client for the OCS2 ITC service. As new observing modes are added we will need to add structure to support them. In some cases ITC inputs are defaulted; look at the json encoders to see exactly what it sent to the server.

The entry point is `ItcImpl` which provides an `Itc` implementation. Everything else is internal.