

<html>
    <head>
        <!--NOTE: bootstrap template Srolling-nav was used -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
          <!-- Bootstrap core CSS -->
        <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <!--    <link href="css/simple-sidebar.css" rel="stylesheet"> -->

    <link href="css/scrolling-nav.css" rel="stylesheet">
    <link href="customcss.css" rel="stylesheet">

        <title>Food Rescue</title>
 
    </head>

    <body id="page-top">

        <nav class="navbar navbar-expand-lg navbar-dark bg-red fixed-top" id="mainNav">
            <div class="container">
                    <a class="title navbar-brand js-scroll-trigger" href="#page-top">Food Rescue</a>
                <div class="collapse navbar-collapse" id="navbar-responsive">
                    <ul class="navbar-nav ml-auto">
                        <li class="nav-item">
                                <img src="burger_bell.png" class="img">
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <header>
            <br>
        <div class="container text-center box">
        <h2>Create New Food Event</h2>

                <form method = "POST" action="login/auth_poster_web.php" id="f">
                <p class="p2">Select Nearest Building</p>
                <select id="Location" class="select" onchange="getLocation()" style="border-style: double;
                color:white; font-weight: bold;">
                        <option value="0" class="options">ART - Art Building</option>
                        <option value="1" class="options">CAD - Cadet Hall</option>
                        <option value="2" class="options">CEB - Computing and Engineering Building</option>
                        <option value="3" class="options">CHN - Cheney Hall</option>
                        <option value="4" class="options">CMC - Communications Building</option>
                        <option value="5" class="options">HAR - Hargreaves Hall</option>
                        <option value="6" class="options">HUS - Huston Hall</option>
                        <option value="7" class="options">ISL - Isle Hall</option>
                        <option value="8" class="options">JFK - JFK Library</option>
                        <option value="9" class="options">KGS - Kingston Hall</option>
                        <option value="10" class="options">MAL - Campus Mall</option>
                        <option value="11" class="options">MAR - Martin Hall</option>
                        <option value="12" class="options">MON - Monroe Hall</option>
                        <option value="13" class="options">MUS - Music Building</option>
                        <option value="14" class="options">PAT - Patterson Hall</option>
                        <option value="15" class="options">PAV - Special Events Pavilion</option>
                        <option value="16" class="options">PUB - Pence Union Building</option>
                        <option value="17" class="options">RTV - Radio-TV Building</option>
                        <option value="18" class="options">SCI - Science Building</option>
                        <option value="19" class="options">SHW - Showalter Hall</option>
                        <option value="20" class="options">SNR - Senior Hall</option>
                        <option value="21" class="options">SUT - Sutton Hall</option>
                        <option value="22" class="options">THE - University Theatre</option>
                        <option value="23" class="options">URC - Recreation Center</option>
                        <option value="24" class="options">WLM - Williamson Hall</option>	
                    </select>
                    <br>
                    <p>or customize exact location (right click):</p>
                    <br>
                    <p class="p3">Select estimated time of availability:</p>
                    <select name="duration" id="time" class="select2" style="border-style: double;color:white; font-weight: bold;">
                        <option value="15" class="options"> less than 15 minutes</option>
                        <option value="30" class="options"> less than 30 minutes</option>
                        <option value="60" class="options"> less 1 hour</option>
                    </select>

                   <div id="map"></div>
                    <br>

                    <input type="hidden" name="title" id="title" value="">

                    <input type="hidden" name="lat" id="Lat" style="border-style: double;color:black;"><br><br>
                    <input type="hidden" name="long" id="Long" style="border-style: double;color:black;"><br><br>
                    <input type="hidden" name="data" id="data">

                    <?php
                            echo "<input type='hidden' name='auth' value = " . $_GET['token'] .">";
                    ?>
               <!--     <button onclick="getValues()" style="position: absolute; left:0%;">click here</button> -->
               <textarea name="body" class="comment" form="f" ></textarea>

                   <input type="submit" class="sub" name="subnotification" onclick="getValues()" value="SEND ANNOUNCEMENT">
            

                </form>
                <div style="position: absolute; bottom: 5%; right: 10; text-align:right; color:white; z-index:2;">Powered by Google Maps API</div>
                <div style="position: absolute; bottom: 0%; right: 10; text-align:right; color:white; z-index:2;">Base template Scrolling Nav by Bootstrap</div>
        </div>
    </header>

  
        <script src = "mapFunctions.js"></script>
        <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDXWwdA4ONNbGRTI3tnx40rFsOOO4va_JI&callback=initMap"></script>
        <script src="vendor/jquery/jquery.min.js"></script>
        <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

        </script>
    </body>

</html>