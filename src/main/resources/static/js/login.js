// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

// 表單提交事件處理
const handleFormSubmit = async (event) => {

	event.preventDefault(); // 停止表單的預設傳送行為，改成自訂行為

	const formData = {
		userEmail: $('#userEmail').val(),
		userPassword: $('#userPassword').val()
	};

	// console.log(formData);
	await login(formData);
};

const login = async (formData) => {
	try {
		// 從後端獲取 CSRF Token
		const csrfResponse = await fetch(`http://${ip}:8080/tinglinews/users/csrf-token`, {
			method: 'GET',
			credentials: 'include' // 需要包含cookie資訊以獲取CSRF Token
		});

		const { state: csrfState, message: csrfMessage, data: csrfData } = await csrfResponse.json();
		console.log(csrfState, csrfMessage, csrfData);

		if (!csrfData.csrfToken) {
			Swal.fire('未取得 CSRF Token', '', 'warning');
			return;
		}
		// 在登入請求中包含 CSRF Token
		const response = await fetch(`http://${ip}:8080/tinglinews/users/login`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'X-CSRF-Token': csrfData.csrfToken
			},
			body: JSON.stringify(formData),
			credentials: 'include'
		});

		const { state, message, data } = await response.json();
		// console.log(state, message, data);

		// 根據登入狀態進行跳轉
		if (!state || !data || !data.userId) {
			Swal.fire(message, '', 'error');
			return;
		}

		// 將 userId 儲存至 sessionStorage（判斷用戶是否已登入）
		sessionStorage.setItem('userData', JSON.stringify(data));
		sessionStorage.setItem('userId', data.userId);
		// 顯示於 nav-login #welcome
		sessionStorage.setItem('userName', data.userName);
		// 判斷權限 顯示後台按鈕
		sessionStorage.setItem('authorityId', data.authority.authorityId);

		// 如果沒有 email 跳轉至個人資料頁面
		const email = sessionStorage.getItem('userEmail');
		if (!email) {
			Swal.fire(message, '', 'success');
			setTimeout(() => {
				window.location.replace('/tinglinews/user/profile.html');
			}, 1000);
			return;
		}
		// 如果有 email 則跳轉至贊助頁面
		Swal.fire(message, '', 'success');
		setTimeout(() => {
			sessionStorage.setItem('userEmail', data.userEmail);
			window.location.replace('/tinglinews/user/donate.html');
		}, 1000);


	} catch (e) {
		console.error('登入錯誤：', e);
		Swal.fire('登入錯誤 請稍後再試', e.message || e, 'error');
	}

};


$(document).ready(() => {

	$('.header-container').load('../nav.html');
	$('.footer-container').load('../footer.html');

	const email = JSON.parse(sessionStorage.getItem('userData'))?.userEmail || sessionStorage.getItem('userEmail');
	if(email){
		$('#userEmail').val(email);
	}
	
	$('#login-form').on('submit', handleFormSubmit);

	$('#forget').on('click', () => {
		sessionStorage.setItem('userEmail', $('#userEmail').val());
		window.location.replace('/tinglinews/user/forget.html');
	});

	$('#github').on('click', () => {
		const clientId = 'Ov23liDtaaE6SzdeiYZu';
		const redirectUri = `http://172.20.10.5:8080/tinglinews/callback/github`;
		const githubAuthUrl = `https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}`;
		window.location.replace(githubAuthUrl);
	});

});