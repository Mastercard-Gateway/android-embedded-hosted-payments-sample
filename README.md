#+title: README
#+author: Stewart Boling

This project is an example of how to show Simplify's Hosted Payment in a page to collect a Card Token then perform a 
redirect that can be consumed by a WebView in an Android App. The Card Token can then be used to complete a Payment 
using any of Simplify's server-side SDKs.

The example PHP is hostedPayment.php when Hosted Payment returns a card token to the page the WebView is redirected to 
simplify://cardToken?=<Card Token>

The embed WebView listens for the redirect for the simplify:// scheme and can the parse the URL for the card token.


* License

Apache License, version 2.0.

© Copyright 2019 