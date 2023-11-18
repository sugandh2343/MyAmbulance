<?php

include 'connection.php';




?>

<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="UTF-8">
	<title>Contact-Us</title>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css">
	<!-- Font awesome -->
	<link rel="stylesheet" type="text/css" href="css/font-awesome.min.css">
	<link rel="stylesheet" type="text/css" href="css/login1.css">

	<!-- Google Fonts -->
	<link href="https://fonts.googleapis.com/css?family=Raleway" rel="stylesheet">
	<link href="https://fonts.googleapis.com/css?family=Cookie" rel="stylesheet">
	<link href="https://fonts.googleapis.com/css?family=Kaushan+Script" rel="stylesheet">
	<link href="https://fonts.googleapis.com/css?family=Kaushan+Script|Wendy+One" rel="stylesheet">
	<link href="https://fonts.googleapis.com/css?family=Didact+Gothic" rel="stylesheet">
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular.min.js"></script>
</head>

<body>
	<section class="con">
		<div class="overlay">
			<div class="container">

				<div class="row justify-content-center">
					<div class="col-md-8 p-3 mt-5">
						<h2 class="text-center display-3"><Strong>Contact-Us</Strong></h2>
					</div>
				</div>
				<div class="row h-50 justify-content-center">
					<div class="col-md-8 main-con p-3 justify-content-center">
						<form action="" method="POST" class="form-horizontal">
							<div class="form-group row justify-content-center">
								<div class="col-md-10 mb-5">
									<div class="wrapper">
										<input type="text" name="myname"id="username" class="in" placeholder="Name">
										<span class="underline"></span>
									</div>

								</div>
							</div>
							<div class="form-group row">
								<div class="col-md-10 offset-md-1 mb-5">
									<div class="wrapper">
										<input type="email" name="myemail" id="email" class="in" placeholder="Enter Your Email">
										<span class="underline"></span>

									</div>

								</div>
							</div>
							<div class="form-group row">
								<div class="col-md-10 offset-md-1 mb-5">
									<div class="wrapper">
										<input type="text" id="text" name="message" class="in" placeholder="Enter message">
										<span class="underline"></span>

									</div>

								</div>
							</div>

							<div class="form-group row justify-content-center">
								<div class="col-md-6">
									<button type="submit" class="btn btn-primary btn-block" onclick="validate()">Send
										Message</button>

									<br>
									<p>
										<font color=white>
											<font size=3px;>Our Address:- <br>
												<li><i class="ti-map"></i>19/10 XXXXXXXXX XXXXXXX XXXXXXXX</li>
												<li><i class="ti-mobile"></i>+ 999 999 9999</li>
												<li><i class="ti-envelope"></i><a href="#">info@yoursite.com</a></li>
												<li><i class="ti-home"></i><a href="#">www.yoursite.com</a></li>
									</p>


									</br>

								</div>

							</div>

						</form>


						<p class="text-center t">Brought To You By: code-projects</p>
					</div>
				</div>
			</div>
	</section>

</body>
<!-- Scripts in case you need them :P -->
<!-- Jquery -->
<script src="https://code.jquery.com/jquery-3.1.1.slim.min.js"></script>
<!-- Tether -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js"></script>
<!-- Bootstrap js -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js"></script>
<script src="js/login.js"> </script>
</body>

</html>

<?php

if($_POST)

{

	extract($_POST);
	mysqli_query($con,"INSERT INTO contactus VALUES('','$myname','$myemail','$message')");

	 echo "<script>alert('We will Contact with you soon....');</script>";

}





?>