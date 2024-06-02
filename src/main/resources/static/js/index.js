

// // 定義一個非同步函數來加載 HTML 內容
// const loadHTML = async (uri, containerId) => {
// 	const url = 'http://localhost:8080/tinglinews' + uri;
// 	try {
// 		const response = await fetch(url); // 等待 fetch 請求完成
// 		const data = await response.text(); // 等待回應本文內容
// 		$(containerId).html(data); // 將所得到的本文內容加入到指定容器中；使用 jQuery 設置 innerHTML
// 	} catch (e) {
// 		console.error(e);
// 	}
// };

// const clickSignupBtn = () => {
// 	$('.signup').on('click', async () => {
// 		console.log('點擊註冊按鈕');
// 		$('.main-content').load('user/register.html', clickLoginBtn);
// 	});
// };

// const clickLoginBtn = () => {
// 	$('.login').on('click', async () => {
// 		console.log('點擊登入按鈕');
// 		$('.main-content').load('user/login.html', clickSignupBtn);
// 	});
// }

// 待 DOM 加載完成之後再執行
$(document).ready(() => {

	const currentPath = window.location.pathname;
	console.log(currentPath);
	if (sessionStorage.getItem('userId') != null) {
		$('.header-container').load('nav-login.html', () => {
			console.log('nav.html 加載完畢');
			console.log($('#welcome').text());
		});
	} else {
		$('.header-container').load('nav.html');
	}
	$('.main-content').load('domain.html');
	$('.footer-container').load('footer.html');

});
