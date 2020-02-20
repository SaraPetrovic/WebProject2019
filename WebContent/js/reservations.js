$(document).on('submit', '.bookingTicketForm', function(e) {
	e.preventDefault();
	
	var reservation = {};
	reservation.flightNumber = $("#flightNum").val();
	reservation.numOfPassengers = $("#numOfPassengers").val();
	reservation.ticketType = $("#ticketType").val();
	$.ajax({
		type : 'POST',
		url : "../WebProjekat/rest/reservation/book",
		contentType : 'application/json',
		dataType : "json",
		processData : false,
		data : JSON.stringify(reservation),
		success : function(rez) {
			swal("Success booking", "Total price " + rez + "e", "success");
		},
		error: function(e){
			if(e.status == 409)
				swal("Sorry", "You are blocked by administrator", "error");
			else
				swal("Sorry", "Not enough free space in the plane", "error");
		}
	});
});

function fillFlightNumberLabel(flightNumber){
	document.getElementById("flightNum").value = flightNumber;
}
