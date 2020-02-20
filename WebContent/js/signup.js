$(document).on('submit', '.signupForm', function(e) {
	e.preventDefault();

	if($("#password").val() != $("#confirmPassword").val()){
		swal("Please, confirm your password again.");
		return false;
	}
	
	var user = {};
	user.username = $("#username").val();
	user.lastName = $("#last_name").val();
	user.firstName = $("#first_name").val();
	user.password = $("#password").val();
	user.phoneNumber = $("#phoneNumber").val();
	user.email = $("#email").val();
	
	$.ajax({
		type : 'POST',
		url : "../WebProjekat/rest/user/signup",
		contentType : 'application/json',
		dataType : "json",
		data : JSON.stringify(user),
		processData: false,
		success : function(data) {
			swal("Success", "You have successfully signed up!", "success");
			setTimeout(function (){
				window.location.href = 'index.html';
				}, 3000); 
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			swal("Try again", "User with the same username already exists!", "error");
		}
	});
});

$(document).on('submit', '.profileForm', function(e) {
	e.preventDefault();

	if($("#passwordChange").val() != $("#confirmPasswordChange").val()){
		swal("Please, confirm your password again.");
		return false;
	}
	
	var user = {};
	user.id = $("#id").val();
	user.firstName = $("#nameChange").val();
	user.lastName = $("#lastNameChange").val();
	user.username = $("#usernameChange").val();
	user.password = $("#passwordChange").val();
	user.phoneNumber = $("#phoneNumberChange").val();
	user.email = $("#emailChange").val();
	
	$.ajax({
		type : 'POST',
		url : "../WebProjekat/rest/user/changeProfile",
		contentType : 'application/json',
		dataType : "json",
		data : JSON.stringify(user),
		processData: false,
		success : function(data) {
			swal("Success", "", "success");
		},
		error: function(data) {
			if(data.status == 409)
				swal("Sorry", "You are blocked by administrator", "error");
			else
				swal("Try again", "User with same username already exist.", "error");
		}
	});
});