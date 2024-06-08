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
		const csrfResponse = await fetch('http://localhost:8080/tinglinews/user/login', {
			method: 'GET',
			credentials: 'include' // 需要包含cookie資訊以獲取CSRF Token
		});

		const {csrfState, csrfMessage, csrfData} = await csrfResponse.json();
		console.log(csrfData);

		if (!csrfData.csrfToken) {
			Swal.fire('未取得 CSRF Token', '', 'warning');
			return;
		}
		// 在登入請求中包含 CSRF Token
		const response = await fetch('http://localhost:8080/tinglinews/emp/login', {
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
		if (state != true || !data || !data.userId) {
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
		// sessionStorage.setItem('userId', data.userId);
		// sessionStorage.setItem('userName', data.userName);
		// sessionStorage.setItem('authorityId', data.authority.authorityId);
		// sessionStorage.setItem('authorityName', data.authority.authorityName);

		Swal.fire(message, '', 'success');
		setTimeout(() => {
			window.location.replace('/tinglinews/emp/content-management.html');
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