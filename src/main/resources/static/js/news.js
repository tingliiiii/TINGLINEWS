
// 待 DOM 加載完成之後再執行
$(document).ready(() => {
	if (sessionStorage.getItem('userId') != null) {
		$('.header-container').load('nav-login.html');
	} else {
		$('.header-container').load('nav.html');
	}
	$('.footer-container').load('footer.html');
	$('.ad-container').load('ad.html');

});
