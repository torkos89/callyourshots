"use strict"
const VERSION = "A";
  var webSocket,  form1 = document.getElementById("form1");
  var output = document.getElementById("output");
  output.addEventListener("click",function(){form2.children[0].focus()});
  form1.children[0].focus();
  	form1.onsubmit = function(e){
  		e.preventDefault();
  		var name = e.target.children[0].value;
  		
  		webSocket = new WebSocket("ws://"+location.hostname+(location.port=="80"? "" : (":"+location.port))+"/CallYourShots/serverA");
  		webSocket.onopen = function(){
  			webSocket.send("|:|"+name);
  		}
  		webSocket.onmessage = function(e){
  			let div = document.createElement("div");
  			div.innerHTML = e.data.replace(/\n/g,"<br>");
  			let scroll = output.clientHeight+output.scrollTop==output.scrollHeight;
  		  output.appendChild(div)
  		  if(scroll) output.scrollTop = output.scrollHeight;
  		  console.log(e);
  		}
  		webSocket.onclose = function(e){
  			
  		}
  		form1.parentNode.style.display = "none";
  		let form2 = document.getElementById("form2")
  		form2.style.display = "block";
  		form2.children[0].focus();
  		form2.onsubmit = function(e){
  		  e.preventDefault();
  		  webSocket.send(form2.children[0].value);
  		  form2.children[0].value = "";
  		}
  	}
/*
const VERSION = "A";
const CVS = document.getElementById("game");
const CTX = CVS.getContext("2d");
const worldX = CVS.width;
const worldY = CVS.height;
var health; //draw hearts
//var timeUnits;
var session; 
var form1 = document.getElementById("form1");
form1.onsubmit = function(e){
	e.preventDefault();
	session.send(e.target.children[0].value);
	let name = e.target.children[0].value;
	session = new WebSocket("ws://"+location.hostname+(location.port=="80"? "" : (":"+location.port))+"/CallYourShots/serverA");
	
	session.onopen = function(){
		session.send("|:|"+name);
	}
	session.onmessage = function(e){
		let div = document.createElement("div");
		div.innerHTML = e.data;
		document.getElementById("output").appendChild(div);
		console.log(e);
	}
	session.onclose = function(e){
		
	}
	form1.parentNode.style.display = "none";
	let form2 = document.getElementById("form2")
	form2.style.display = "block";
	form2.onsubmit = function(e){
	  e.preventDefault();
	  session.send(form2.children[0].value);
	  form2.children[0].value = "";
	}
}
*/