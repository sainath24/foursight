var map = L.map('map').setView([12.94, 77.59], 9);
var cowIcon = L.icon({
  iconUrl: 'cow.png',
  iconSize:     [40.8, 33.78], 
  iconAnchor:   [20.4, 33.78], 
  popupAnchor:  [-3, -76] 
});

var potHole = L.icon({
  iconUrl: 'pot.png',
  iconSize:     [36, 36], 
  iconAnchor:   [18, 18], 
  popupAnchor:  [-3, -76] 
});


L.tileLayer('https://api.maptiler.com/maps/streets/{z}/{x}/{y}.png?key=Pf0CDx5DvCpY0zDpU4eY',{
  tileSize: 512,
  zoomOffset: -1,
  minZoom: 1,
  attribution: '<a href="https://www.maptiler.com/copyright/" target="_blank">&copy; MapTiler</a> <a href="https://www.openstreetmap.org/copyright" target="_blank">&copy; OpenStreetMap contributors</a>',
  crossOrigin: true
  }).addTo(map);
  
//L.marker([13.046146, 80.200452], {icon: cowIcon}).addTo(map).bindPopup("I am Shraddhaa's cow.");
//L.marker([13.039746, 80.197295], {icon: cowIcon}).addTo(map).bindPopup("I am Anam's cow.");

var firebaseConfig = {
  apiKey: "AIzaSyCpe4EOnP6m9gnwvH-p3UGSVCSZgNXvLdY",
  authDomain: "cartalyst-2e70c.firebaseapp.com",
  databaseURL: "https://cartalyst-2e70c.firebaseio.com",
  projectId: "cartalyst-2e70c",
  storageBucket: "cartalyst-2e70c.appspot.com",
  messagingSenderId: "515379928218",
  appId: "1:515379928218:web:fd198d7ae94a919b275323"
};

firebase.initializeApp(firebaseConfig);


var query = firebase.database().ref("Complaints").orderByKey();
query.once("value")
  .then(function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      
      var key = childSnapshot.key;
      console.log(key);
      
      var childData = childSnapshot.val();
      var add = childData["address"];
      var cid = childData["cid"];
      var lat = childData["latitude"];
      var long = childData["longitude"];
      var loc = childData["locality"];
      var n = childData["numOfCows"];
      var uid = childData["uid"];

      console.log(n)

      L.marker([lat, long], {icon: cowIcon}).addTo(map).bindPopup("<b>User ID :</b> " + String(uid) + "<br><b>No. of Cows :</b>  " + String(n) + "<br><b>Locality :</b> " + String(loc) + "<br><b>Address :</b> " + String(add));

      
  });
});

var query = firebase.database().ref("Complaints").orderByKey();
query.once("value")
  .then(function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      
      var key = childSnapshot.key;
      console.log(key);
      
      var childData = childSnapshot.val();
      var add = childData["address"];
      var cid = childData["cid"];
      var lat = childData["latitude"];
      var long = childData["longitude"];
      var loc = childData["locality"];
      var n = childData["numOfCows"];
      var uid = childData["uid"];

      console.log(n)

      L.marker([lat, long], {icon: cowIcon}).addTo(map).bindPopup("<b>User ID :</b> " + String(uid) + "<br><b>No. of Cows :</b>  " + String(n) + "<br><b>Locality :</b> " + String(loc) + "<br><b>Address :</b> " + String(add));

      
  });
});

var query2 = firebase.database().ref("Potholes").orderByKey();
query2.once("value")
  .then(function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      
      var key = childSnapshot.key;
      console.log(key);
      
      var childData = childSnapshot.val();
      var add = childData["address"];
      var cid = childData["cid"];
      var lat = childData["latitude"];
      var long = childData["longitude"];
      var loc = childData["locality"];
      var n = childData["numOfCows"];
      var uid = childData["uid"];

      console.log(n)

      L.marker([lat, long], {icon: potHole}).addTo(map).bindPopup("<b>User ID :</b> " + String(uid) + String(n) + "<br><b>Locality :</b> " + String(loc) + "<br><b>Address :</b> " + String(add));

      
  });
});




/*  ref.on("value", function(snapshot) {
   test = snapshot.val();
   console.log(test);


}, function (error) {
   console.log("Error: " + error.code);
}); */