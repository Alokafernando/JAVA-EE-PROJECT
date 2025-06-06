const apiUrl = "http://localhost:8080/EMS_Web_exploded/api/v1/signin";

$('#sign-in').on('click', function (){

    let email = $('#email').val();
    let pass = $('#password').val();


    $.ajax({
        method: 'POST',
        url: apiUrl,
        contentType: 'application/json',
        data: JSON.stringify({
            uemail: email,
            upassword: pass
        }),
        success: function(response){
            if(response.code === '200'){
                localStorage.setItem('uemail', email);
                window.location.href = 'dashboard.html';
            }else{
                alert('Error: ' + response.message);
            }
        }
    });

});
