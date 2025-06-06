const apiUrl = "http://localhost:8080/EMS_Web_exploded/api/v1/signup";

$('#signup').on('click', function (){
    let name = $('#name').val();
    let mail = $('#email').val();
    let pass = $('#password').val();

    $.ajax({
        type: 'POST',
        url: apiUrl,
        data: JSON.stringify({
            uname: name,
            uemail: mail,
            upassword: pass
        }),
        contentType: 'application/json',
        success: function(response){
            console.log(response.code)
            if(response.code === '200'){
                window.location.href = 'SignIn.html'
            }else{

            }
        },
        error: function(xhr, status, err){
            console.error("Signup failed:", err);
            alert("Signup failed! Please try again later.");
        }

    });


});


