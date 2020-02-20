function deleteFlight(id){
	$.ajax({
		type : 'DELETE',
		url : "../WebProjekat/rest/flight/delete/" + id,
		dataType : "json",
		processData: false,
		success : function(rez) {
			location.reload();
		},
		error: function(e){
			swal("Sorry", "You cannot delete a flight for which reservations are available!", "error");
		}
	});
}

function fillDestinationSelects(){
	$("#startSelect").empty();
	$("#endSelect").empty();
	$.ajax({
		type: "GET",
		url: "../WebProjekat/rest/destination/getAllActive",
		success: function(destinations){
			var startSelect = document.getElementById("startSelect");
			var endSelect = document.getElementById("endSelect");
			var opt1 = document.createElement("OPTION");
			var opt2 = document.createElement("OPTION");
			startSelect.add(opt1);
			endSelect.add(opt2);
			opt1.text = "Choose start destination";
			opt1.selected = true;
			opt1.value = 0;
			opt1.disabled = true;
			opt2.text = "Choose end destination";
			opt2.selected = true;
			opt2.value = 0;
			opt2.disabled = true;
			$.each(destinations, function(i, destination){
				var option1 = document.createElement("OPTION");
				var option2 = document.createElement("OPTION");
				startSelect.add(option1);
				option1.value = destination.id;
				option1.text = destination.name;
				endSelect.add(option2);
				option2.value = destination.id;
				option2.text = destination.name;
			});
		}
	});
}

$(document).on('submit', '.flightForm', function(e) {
	e.preventDefault(); // if you're using AJAX
    
	var flight = {};
	var startDestination = {};
	var endDestination = {};
	var date = new Date($("#date").val());
	flight.flightNumber = $("#flightNumber").val();
	startDestination.id = $("#startSelect").val();
	flight.startDestination = startDestination;
	endDestination.id = $("#endSelect").val();
	flight.endDestination = endDestination;
	flight.priceFirst = $("#priceFirst").val();
	flight.priceBusiness = $("#priceBusiness").val();
	flight.priceEconomic = $("#priceEconomic").val();
	flight.firstClass = $("#firstClass").val(); 
	flight.businessClass = $("#businessClass").val();
	flight.economicClass = $("#economicClass").val();
	flight.date = date;
	flight.flightType = $("#flightType").val(); 
	
	$.ajax({
		type : 'POST',
		url : "../WebProjekat/rest/flight/add",
		contentType : 'application/json',
		dataType : "json",
		processData : false,
		data : JSON.stringify(flight),
		success : function(data) {
			location.reload();
		},
		error : function() {
			swal("Try again", "Check flight number", "error");
		}
	});
});

//fill form for change flight
function fillChangeForm(id){
	$.ajax({
		type : 'GET',
		processData: false,
		contentType: 'application/json',
		url : "../WebProjekat/rest/flight/get/" + id,
		success : function(flight) {
			document.getElementById("idFlight").value = flight.id;
			document.getElementById("number").value = flight.flightNumber;
			document.getElementById("changePriceFirst").value = flight.priceFirst;
			document.getElementById("changePriceBusiness").value = flight.priceBusiness;
			document.getElementById("changePriceEconomic").value = flight.priceEconomic;
			document.getElementById("changeFirstClass").value = flight.firstClass;
			document.getElementById("changeBusinessClass").value = flight.businessClass;
			document.getElementById("changeEconomicClass").value = flight.economicClass;
		},
		error: function(e){
			swal("error", "", "error");
		}
	});
}

$(document).on('submit', '.changeFlightForm', function(e){
	e.preventDefault();
	
	var flight = {};
	flight.id = $("#idFlight").val();
	flight.priceFirst = $("#changePriceFirst").val();
	flight.priceBusiness = $("#changePriceBusiness").val();
	flight.priceEconomic = $("#changePriceEconomic").val();
	flight.firstClass = $("#changeFirstClass").val();
	flight.businessClass = $("#changeBusinessClass").val();
	flight.economicClass = $("#changeEconomicClass").val();
	
	$.ajax({
		type: 'POST',
		url: "../WebProjekat/rest/flight/change",
		contentType: 'application/json',
		dataType: 'json',
		processData: false,
		data: JSON.stringify(flight),
		success: function(data){
			location.reload();
		},
		error: function(e){
			swal("error", "", "error");
		}
	});
});

function fillFilterOption(){
	 var value = $("#filterOption").val();
	 $("#criteriaSelect").empty();
	 var select = document.getElementById("criteriaSelect");
	 if(value == "date"){
		var option = document.createElement("OPTION");
		select.add(option);
		option.value = "date";
		option.text = "The latest";
	 }else if(value == "flightCode"){
		var option = document.createElement("OPTION");
		select.add(option);
		option.value = "code";
		option.text = "Sorted";
	 }else if(value == "flightClass"){
		 $.ajax({
				type: "GET",
				url: "../WebProjekat/rest/flight/getFlightTypes",
				success: function(types){
					var select = document.getElementById("criteriaSelect");
					$.each(types, function(i, type){
						var option = document.createElement("OPTION");
						select.add(option);
						option.value = type;
						option.text = type;
					});
				},
			});
	 }
}

function filter(){
	var param =  $("#criteriaSelect").val();
	$.ajax({
		type : 'GET',
		url : "../WebProjekat/rest/flight/filter/" + param,
		contentType : 'application/json',
		dataType : "json",
		processData : false,
		success : function(flights) {
			swal("Success", flights.length, "success");
			document.getElementById("flights").innerHTML = " ";
			
			$.ajax({
				type : 'GET',
				url : "../WebProjekat/rest/user/loggedUser",
				dataType : "json",
				processData : false,
				success : function(user) {
					if(user.admin == false){
						$.each(flights, function(i, flight){
							var datum = new Date(flight.date)
							var panel = "";
							panel += '<div class="panel panel-info col-md-6" style="width:90%;margin-left:60px;">';
							panel += '<div class="panel-heading">Flight number: ' + flight.flightNumber + '</div>';
							panel += '<div class="panel-body"><div class="row"><div class="col-md-6"><label>Start destination: ' + flight.startDestination.name.toUpperCase() + '<br>End destination: ' + flight.endDestination.name.toUpperCase() + '<br>First class price: ' + flight.priceFirst + 'e' + '<br>Business class price: ' + flight.priceBusiness + 'e' + '<br>Economic class price: ' + flight.priceEconomic + 'e' + '<br>Date: ' + datum.toLocaleString() + '<br>Flight type: ' + flight.flightType + '</label></div></div><br>';
							panel += '<div class="col-md-6"><button onclick="fillFlightNumberLabel(\'' + flight.flightNumber + '\')" data-toggle="modal" data-target="#bookingTicketModal" class="btn btn-success green"><i class="fa fa-share"></i>Booking ticket for this flight</button></div></div><br>';
							panel += '<div id="body' + flight.id + '"></div></div>';
							$("#flights").append(panel);
							
						});
					}else{
						$.each(flights, function(i, flight){
							var datum = new Date(flight.date)
							var panel = "";
							panel += '<div class="panel panel-info col-md-6" style="width:90%;margin-left:60px;">';
							panel += '<div class="panel-heading">Flight number: ' + flight.flightNumber + '&nbsp;&nbsp;<button onclick="fillChangeForm(\'' + flight.id + '\')" class="btn btn-warning" data-toggle="modal" data-target="#changeFlightModal"><i class="fa fa-share"></i>Change</button>&nbsp;&nbsp;<button onclick="deleteFlight(' + flight.id + ');" class="btn btn-danger">Delete</button></div>';
							panel += '<div class="panel-body"><div class="row"><div class="col-md-6"><label>Start destination: ' + flight.startDestination.name.toUpperCase() + '<br>End destionation: ' + flight.endDestination.name.toUpperCase() + '<br>First class price: ' + flight.priceFirst + 'e' + '<br>Business class price: ' + flight.priceBusiness + 'e' + '<br>Economic class price: ' + flight.priceEconomic + 'e' + '<br>Date: ' + datum.toLocaleString() + '<br>Flight type: ' + flight.flightType + '</label></div></div></div>';
							panel += '<div id="bodya' + flight.id + '"></div></div>';
							$("#flights").append(panel);
						});
					}
				},
				error : function() {
					swal("", "You must be logged", "error");
				}
			});
		},
		error : function() {
			swal("Try again", "Check flight number", "error");
		}
	});
}

function search(){
	var input =  $("#searchInput").val();
	var date =  $("#searchDate").val();
	
	if (input == "" && date == "") {
		swal("", "You must enter at least one of two search parameters", "error");
	  	return;
	}
	
	$.ajax({
		type : 'GET',
		url : "../WebProjekat/rest/flight/search?first=" + input + "&second=" + date,
		dataType : "json",
		processData : false,
		success : function(flights) {
			swal("Success", flights.length, "success");
			document.getElementById("flights").innerHTML = " ";
			
			$.ajax({
				type : 'GET',
				url : "../WebProjekat/rest/user/loggedUser",
				dataType : "json",
				processData : false,
				success : function(user) {
					if(user.admin == false){
						$.each(flights, function(i, flight){
							var datum = new Date(flight.date)
							var panel = "";
							panel += '<div class="panel panel-info col-md-6" style="width:90%;margin-left:60px;">';
							panel += '<div class="panel-heading">Flight number: ' + flight.flightNumber + '</div>';
							panel += '<div class="panel-body"><div class="row"><div class="col-md-6"><label>Start destination: ' + flight.startDestination.name.toUpperCase() + '<br>End destination: ' + flight.endDestination.name.toUpperCase() + '<br>First class price: ' + flight.priceFirst + 'e' + '<br>Business class price: ' + flight.priceBusiness + 'e' + '<br>Economic class price: ' + flight.priceEconomic + 'e' + '<br>Date: ' + datum.toLocaleString() + '<br>Flight type: ' + flight.flightType + '</label></div></div><br>';
							panel += '<div class="col-md-6"><button onclick="fillFlightNumberLabel(\'' + flight.flightNumber + '\')" data-toggle="modal" data-target="#bookingTicketModal" class="btn btn-success green"><i class="fa fa-share"></i>Booking ticket for this flight</button></div></div><br>';
							panel += '<div id="body' + flight.id + '"></div></div>';
							$("#flights").append(panel);
							
						});
					}else{
						$.each(flights, function(i, flight){
							var datum = new Date(flight.date)
							var panel = "";
							panel += '<div class="panel panel-info col-md-6" style="width:90%;margin-left:60px;">';
							panel += '<div class="panel-heading">Flight number: ' + flight.flightNumber + '&nbsp;&nbsp;<button onclick="fillChangeForm(\'' + flight.id + '\')" class="btn btn-warning" data-toggle="modal" data-target="#changeFlightModal"><i class="fa fa-share"></i>Change</button>&nbsp;&nbsp;<button onclick="deleteFlight(' + flight.id + ');" class="btn btn-danger">Delete</button></div>';
							panel += '<div class="panel-body"><div class="row"><div class="col-md-6"><label>Start destination: ' + flight.startDestination.name.toUpperCase() + '<br>End destionation: ' + flight.endDestination.name.toUpperCase() + '<br>First class price: ' + flight.priceFirst + 'e' + '<br>Business class price: ' + flight.priceBusiness + 'e' + '<br>Economic class price: ' + flight.priceEconomic + 'e' + '<br>Date: ' + datum.toLocaleString() + '<br>Flight type: ' + flight.flightType + '</label></div></div></div>';
							panel += '<div id="bodya' + flight.id + '"></div></div>';
							$("#flights").append(panel);
						});
					}
				},
				error : function() {
					swal("", "You must be logged", "error");
				}
			});
		},
		error : function() {
			swal("", "You must enter at least one of two search parameters", "error");
		}
	});
}

function filterAndSearch(){
	var filterCriteria = $("#criteriaSelect").val();
	var input = $("#searchInput").val();
	var date = $("#searchDate").val();
	
	if (input == "" && date == "") {
		swal("", "You must enter at least one of two search parameters", "error");
	  	return;
	}
	
	$.ajax({
		type : 'GET',
		url : "../WebProjekat/rest/flight/filterAndSearch?filter=" + filterCriteria + "&input=" + input + "&date=" + date,
		dataType : "json",
		processData : false,
		success : function(flights) {
			swal("Success", flights.length, "success");
			document.getElementById("flights").innerHTML = " ";
			
			$.each(flights, function(i, flight){
				var datum = new Date(flight.date)
				var panel = "";
				panel += '<div class="panel panel-info col-md-6" style="width:90%;margin-left:60px;">';
				panel += '<div class="panel-heading">Flight number: ' + flight.flightNumber + '&nbsp;&nbsp;<button onclick="fillChangeForm(\'' + flight.id + '\')" class="btn btn-warning" data-toggle="modal" data-target="#changeFlightModal"><i class="fa fa-share"></i>Change</button>&nbsp;&nbsp;<button onclick="deleteFlight(' + flight.id + ');" class="btn btn-danger">Delete</button></div>';
				panel += '<div class="panel-body"><div class="row"><div class="col-md-6"><label>Start destination: ' + flight.startDestination.name.toUpperCase() + '<br>End destionation: ' + flight.endDestination.name.toUpperCase() + '<br>First class price: ' + flight.priceFirst + 'e' + '<br>Business class price: ' + flight.priceBusiness + 'e' + '<br>Economic class price: ' + flight.priceEconomic + 'e' + '<br>Date: ' + datum.toLocaleString() + '<br>Flight type: ' + flight.flightType + '</label></div></div></div>';
				panel += '<div id="bodya' + flight.id + '"></div></div>';
				$("#flights").append(panel);
			});
		},
		error : function() {
			swal("", "You must enter at least one of two search parameters", "error");
		}
	});
}

function searchFlights(){
	var flight = {};
	var start = {};
	var end = {};
	start.name = $("#start").val();
	end.name = $("#end").val();
	flight.startDestination = start;
	flight.endDestination = end;
	flight.date = $("#datum").val();
	
	$.ajax({
		type: 'POST',
		url: "../WebProjekat/rest/flight/find",
		contentType: 'application/json',
		dataType: 'json',
		processData: false,
		data: JSON.stringify(flight),
		success: function(flights){
			
			document.getElementById("flights").innerHTML = " ";
			
			$.each(flights, function(i, flight){
				var datum = new Date(flight.date)
				var panel = "";
				panel += '<div class="panel panel-info col-md-6" style="width:90%;margin-left:60px;">';
				panel += '<div class="panel-heading">Flight number: ' + flight.flightNumber + '</div>';
				panel += '<div class="panel-body"><div class="row"><div class="col-md-6"><label>Start destination: ' + flight.startDestination.name.toUpperCase() + '<br>End destination: ' + flight.endDestination.name.toUpperCase() + '<br>First class price: ' + flight.priceFirst + 'e' + '<br>Business class price: ' + flight.priceBusiness + 'e' + '<br>Economic class price: ' + flight.priceEconomic + 'e'  + '<br>Date: ' + datum.toLocaleString() + '<br>Flight type: ' + flight.flightType + '</label></div></div><br>';
				panel += '<div class="col-md-6"><button onclick="fillFlightNumberLabel(\'' + flight.flightNumber + '\')" data-toggle="modal" data-target="#bookingTicketModal" class="btn btn-success green"><i class="fa fa-share"></i>Booking ticket for this flight</button></div></div><br>';
				panel += '<div id="body' + flight.id + '"></div></div>';
				$("#flights").append(panel);
				
			});
			
		},
		error: function(e){
			swal("error", "", "error");
		}
	});
}
