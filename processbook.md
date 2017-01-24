# Jan 9:
- Project proposals and other stuff.
# Jan 11:
- Added all the design documents.
# Jan 12:
- Created the project prototype, added a lot of TODOs
# Jan 13:
- Added the location stuff from the android samples, and the Route, POI base classes.
- Decided not to create another MapsActivity when a user accepts the route, but to just hide the buttons on the map.
# Jan 14:
- Added the Contstants class and the Geofencing stuff for the POI's.
- Also added the Place autocompletion for the from and to addresses.
- The geofencing does not work yet, but is not part of my MVP I believe.
# Jan 15:
- The Route class now extends the AsyncTask class so that all the server stuff is handled in this class. This seperates the concerns further. Also added a seperate Activity for displaying the information from a POI.
# Jan 16:
- Not a lot done today, mostly bugfixes.
- Installed everything on my server so that I can start testing that as soon as possible.
# Jan 17:

# Jan 18:
- Added more data from dbpedia and further improved some of the server side stuff.
- Fixed the InformationActivity WebView.
- Refactored the constructor of the POIs to take a JSONObject and then set all the attributes to their value or null.
- Fixed some checks for empty strings concerning POIs to check for null.

# Jan 19:
- Created a new class Point which has a location<LatLng>, this is done to seperate points on the route from POIs which also have other information.
- Created a function to instantiate objects from a JSONArray with a certain class. This is used in the Route class.
- Decided to hold off on the Geofencing until week three as a lot more code is needed to get that working.

# Jan 20:
- Mostly worked on getting the communication with the server working. I am now using the Webb package to do requests because doing them in pure Java is just horrible... This has resulted in my app working almost as planned and will continue later on to add more features.
- The communication with the server works.

# Jan 23:

- Spent my day on integrating firebase authentication and storing of routes in firebase. Also created a menu for the user to log in, create an account, view saved routes and sign out.
- Created a view for the saved routes together with an adapter, this still has to be tested.
- I can't store a route yet do to a key error in my Route class.
- Added a function to store the routes in the mapsActivity.
