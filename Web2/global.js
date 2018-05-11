var auth = $("#auth").val();
//Get navbar
$.get("api/navigate/",
	{
		"auth": auth
	},
	function addNavigationElements(results) {
		//TODO: Append the <li> from results
		$("#navbar").appendChild();
	}
);


function downloadApp() {
	document.getElementById('iframeAppDownloader').src = "../assets/FoodRescue-18.5.9.apk";
}