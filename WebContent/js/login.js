$(document).on('submit', '.LoginForm', function(e){
	e.preventDefault();
	
	var user = {};
	user.username = $("#usernameInput").val();
	user.password = $("#passwordInput").val();
	
	$.ajax({
		type: 'POST',
		url : "../WebProjekat/rest/user/login",
		contentType : 'application/json',
		dataType : "json",
		data : JSON.stringify(user),
		processData : false,
		success : function(data){
			window.location.href = 'profile.html';
		},
		error : function(err){
			swal("Error", "Try again!", "error");
		}
	});
});