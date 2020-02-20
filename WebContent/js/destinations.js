$.ajax({
	type : 'GET',
	url : "../WebProjekat/rest/destination/getAll",
	dataType : "json",
	success : function(destinations) {
		$.each(destinations, function(index, destination) {
			var button;
			if(destination.active)
				button = "Archive";
			else
				button = "Activate"
			var panel = '<div class="panel panel-default" style="width:90%; margin-left:60px;">';
			panel += '<div class="panel-heading"><h3>' + destination.name + '</h3></div>';
			panel += '<div class="panel-body">&nbsp; Country: ' + destination.country + '&nbsp;&nbsp; Airport name: ' + destination.airportName + '&nbsp;&nbsp; Airport code: ' + destination.airportCode + '<button id="change" onclick="fillFormForChangeDestination('+destination.id+');" value="' + destination.id + '" data-toggle="modal" data-target="#changeDestinationModal" style="margin-left:80%;" class="btn btn-primary">Change</button>&nbsp; <button id="delete" onclick="deleteDestination(\''+destination.id +'\',\'' + button+'\');" value="' + destination.id + '" class="btn btn-danger">' + button + '</button>';
			
			panel += '</div></div>';
			$('#group').append(panel);
		});
	}
});

$(document).on('submit', '.destinationForm', function(e) {
	e.preventDefault(); // if you're using AJAX
    
	var destination = {};
	destination.name = $("#destinationName").val();
	destination.country = $("#country").val();
	destination.airportName = $("#airportName").val();
	destination.airportCode = $("#airportCode").val();
	
		$.ajax({
			type : 'POST',
			url : "../WebProjekat/rest/destination/add",
			contentType : 'application/json',
			dataType : "json",
			processData : false,
			data : JSON.stringify(destination),
			success : function(data) {
				location.reload();
			},
			error : function() {
				swal("Try again", "Destination with the same name already exists", "error");
			}
		});
});

$(document).on('submit', '.changeDestinationForm', function(e){
	e.preventDefault();
	
	var destination = {};
	destination.id = $("#idDestination").val();
	destination.airportName = $("#changeAirportName").val();
	destination.airportCode = $("#changeAirportCode").val();
	
	$.ajax({
		type: 'POST',
		url: "../WebProjekat/rest/destination/change",
		contentType: 'application/json',
		dataType: 'json',
		processData: false,
		data: JSON.stringify(destination),
		success: function(data){
			location.reload();
		},
		error: function(e){
			swal("error", "", "error");
		}
	});
});

function deleteDestination(id, buttonName){
	if(buttonName === "Activate"){
		$.ajax({
			type: 'POST',
			url: "../WebProjekat/rest/destination/activate/" + id,
			contentType: 'application/json',
			dataType: "json",
			processData: false,
			success: function(destination){
				location.reload();
			},
			error: function(e){
				swal("error", "", "error");
			}
		});	
	}else{
		$.ajax({
			type: 'POST',
			url: "../WebProjekat/rest/destination/archive/" + id,
			contentType: 'application/json',
			dataType: "json",
			processData: false,
			success: function(destination){
				location.reload();
			},
			error: function(e){
				swal("error", "", "error");
			}
		});	
	}
	
}

function fillFormForChangeDestination(id){
	$.ajax({
		type : 'POST',
		processData: false,
		contentType: 'application/json',
		url : "../WebProjekat/rest/destination/get/" + id,
		success : function(destination) {
			document.getElementById("changeDestinationName").value = destination.name;
			document.getElementById("changeCountry").value = destination.country;
			document.getElementById("changeAirportName").value = destination.airportName;
			document.getElementById("changeAirportCode").value = destination.airportCode;
			document.getElementById("idDestination").value = destination.id;
		},
		error: function(e){
			swal("error", "", "error");
		}
	});
}
