// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

// 表單提交事件處理
const handleSubmit = async (event) => {

	event.preventDefault(); // 停止表單的預設傳送行為，改成自訂行為

	const formData = {
		userEmail: $('#userEmail').val(),
		userPassword: $('#userPassword').val()
	};

	await login(formData);
};

const login = async (formData) => {
	// console.log(formData);
	try {
		// 從後端獲取 CSRF Token
		const csrfResponse = await fetch(`http://${ip}:8080/tinglinews/user/login`, {
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
		const response = await fetch(`http://${ip}:8080/tinglinews/user/login`, {
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

		// 檢查使用者權限：只有員工可以登入
		if (data.authority.authorityId < 1) {
			Swal.fire('權限不足', '帳號權限有誤，請聯絡管理員', 'warning');
			return;
		}

		// 將 userId 儲存至 sessionStorage（判斷用戶是否已登入）
		sessionStorage.setItem('userData', JSON.stringify(data));

		Swal.fire(message, '', 'success');
		setTimeout(() => {
			window.location.replace('/tinglinews/emp/index.html');
		}, 1000);
		return;

	} catch (e) {
		console.error('登入錯誤：', e);
		Swal.fire('登入錯誤 請稍後再試', e, 'error');
	}

};


$(document).ready(() => {

	$('.header-container').load('../nav.html');
	$('.footer-container').load('../footer.html');
	$('#emp-login-form').on('submit', handleSubmit);

});