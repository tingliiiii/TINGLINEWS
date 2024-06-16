// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

// 表單提交事件處理
const handleFormSubmit = async (event) => {

	event.preventDefault(); // 停止表單的預設傳送行為，改成自訂行為

	if ($('#userPassword').val() !== $('#passwordConfirm').val()) {
		Swal.fire('密碼不一致', '', 'error');
		return;
	}

	const formData = {
		userName: $('#userName').val(),
		gender: $('input[name="gender"]:checked').val() || 'N/A',
		birthday: $('#birth').val(),
		phone: $('#phone').val(),
		userEmail: $('#userEmail').val(),
		userPassword: $('#userPassword').val()
	};

	await addUser(formData);
};

const addUser = async (formData) => {
	try {
		// 從後端獲取 CSRF Token
		const csrfResponse = await fetch(`http://${ip}:8080/tinglinews/user/login`, {
			method: 'GET',
			credentials: 'include' // 需要包含cookie資訊以獲取 CSRF Token
		});

		const { state: csrfState, message: csrfMessage, data: csrfData } = await csrfResponse.json();
		console.log(csrfState, csrfMessage, csrfData);

		if (!csrfData.csrfToken) {
			Swal.fire('未取得 CSRF Token', '', 'warning');
			return;
		}

		// 在註冊請求中包含 CSRF Token
		const response = await fetch(`http://${ip}:8080/tinglinews/user/register`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'X-CSRF-Token': csrfData.csrfToken
			},
			body: JSON.stringify(formData),
			credentials: 'include'
		});

		const { state, message, data } = await response.json();
		// console.log(message);

		// 根據註冊狀態進行跳轉
		if (state === true && data && data.userId) {
			sessionStorage.setItem('userData', JSON.stringify(data));
			// 將 userId 儲存至 sessionStorage
			sessionStorage.setItem('userId', data.userId);
			sessionStorage.setItem('userName', data.userName);
			sessionStorage.setItem('userEmail', data.userEmail);
			sessionStorage.setItem('authorityId', data.authority.authorityId);
			Swal.fire(message, '', 'success');
			setTimeout(() => {
				window.location.replace('/tinglinews/user/profile.html');
			}, 1000);
		} else {
			Swal.fire(message, '', 'error');
		}
	} catch (error) {
		console.error('註冊請求錯誤：', error);
		Swal.fire('註冊過程中出現錯誤，請稍後再試', '', 'error');
	}

};

$(document).ready(() => {

	$('.header-container').load('../nav.html');
	$('.footer-container').load('../footer.html');

	// 綁定表單提交事件
	$('#register-form').on('submit', handleFormSubmit);

	$('#github').on('click', () => {
		const clientId = 'Ov23liDtaaE6SzdeiYZu';
		const redirectUri = `http://172.20.10.5:8080/tinglinews/callback/github`;
		const githubAuthUrl = `https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}`;
		window.location.replace(githubAuthUrl);
	});
});


// 延伸功能
// 註冊時跳錯誤：email 不可重複註冊