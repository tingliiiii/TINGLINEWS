

// 定義一個非同步函數來加載 HTML 內容
const loadHTML = async (uri, containerId) => {
	const url = 'http://localhost:8080/tinglinews' + uri;
	try {
		const response = await fetch(url); // 等待 fetch 請求完成
		const data = await response.text(); // 等待回應本文內容
		$(containerId).html(data); // 將所得到的本文內容加入到指定容器中；使用 jQuery 設置 innerHTML
	} catch (e) {
		console.error(e);
	}
};

const clickSignupBtn = () => {
	$('.signup').on('click', async () => {
		console.log('點擊註冊按鈕');
		$('.main-content').load('user/register.html', clickLoginBtn);
	});
};

const clickLoginBtn = () => {
	$('.login').on('click', async () => {
		console.log('點擊登入按鈕');
		$('.main-content').load('user/login.html', clickSignupBtn);
	});
}

// 待 DOM 加載完成之後再執行
$(document).ready(() => {

	$('.header-container').load('nav.html', () => {
		/*
				// 控制 nav 區塊被點擊時就關閉 offcanvas
				
				$('#main-menu').on('click', () => {
					var offcanvasInstance = bootstrap.Offcanvas.getInstance($('#main-menu')[0]);
					offcanvasInstance.toggle();
				});
				
		
				$('.login').on('click', async () => {
					console.log('點擊登入按鈕');
					$('.main-content').load('user/login.html', clickSignupBtn);
					// clickSignupBtn();
					// await test();
					// await loadHTML('/user/login.html', '#main-content');
					// loginContainerOnload();
					// console.log(loginContainerOnload());
		
				});
		
				$('.signup').on('click', async () => {
					$('.main-content').load('user/signup.html', clickLoginBtn);
					// await loadHTML('/user/signup.html', '#main-content');
					// clickLoginBtn();
					// signupContainerOnload();
				});
		
				$('.list-all').on('click', async () => {
					$('.main-content').load('list/all.html');
					// await loadHTML('/list/all.html', '#main-content');
				});
				*/
	});

	$('.main-content').load('domain.html', () => {
		/*
		$('.login').on('click', async () => {
			$('.main-content').load('user/login.html');
		});

		$('.signup').on('click', async () => {
			$('.main-content').load('user/signup.html');
		});
		*/
	});

	$('.footer-container').load('footer.html');

	// await loadHTML('/nav.html', '#header-container');
	// await loadHTML('/domain.html', '#main-content');
	// await loadHTML('/footer.html', '.footer-container');

});
