var pos;
pos = 0;

setInterval(function() {
  var oReq = new XMLHttpRequest();
  oReq.addEventListener("load", null);
  oReq.open("GET", "https://yet-another-test-1261.appspot.com/_ah/api/presentIt/v1/getActionToBeTaken?fields=message");
  oReq.onload = function(){
    var jsontext = oReq.responseText;
    var obj = JSON.parse(jsontext);
    //alert(obj.message);
    if(obj.message === "next"){
      pos = pos + 554;
      //alert(pos);
      window.scrollTo(0,pos);
    }
    else if(obj.message === "prev"){
      pos = pos - 554;
      //alert(pos);
      window.scrollTo(0,pos);
    }
    else if(obj.message === "stop"){
      //alert("Presentation Over");
    }
  };
  oReq.send();
}, 1000);
