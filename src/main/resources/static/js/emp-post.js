// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

// 從後端抓資料
const fetchData = async (uri) => {
	try {
		const response = await fetch(`http://${ip}:8080/tinglinews${uri}`); // 等待 fetch 請求完成
		const { state, message, data } = await response.json(); // 等待回應本文內容
		// console.log(state, message, data);
		if (!state) throw new Error(message);
		return data;
	} catch (error) {
		console.error('Fetching data error:', error);
		return [];
	}
};

const createOptionElement = (value, text) =>
	$('<option></option>').attr('value', value).text(text);

// 新增文章的標籤選項
const loadTagOptions = async () => {
	const data = await fetchData('/emp/tags');
	const select = $('#tags');
	const options = data.slice(1).map(tag => createOptionElement(tag.tagId, tag.tagName));
	select.append(options);
};

const loadJournalistOptions = async () => {
	const data = await fetchData('/emp/journalists');
	const select = $('#journalistIds');
	const options = data.map(journalist => createOptionElement(journalist.userId, journalist.userName));
	select.append(options);

	$('#journalistIds').select2({
		placeholder: '請選擇記者'
	});

	// console.log(select.val().length);
	if (select.val().length === 0) {
		// 如果 sessionStorage 中有 userId，預設選中該選項
		const data = JSON.parse(sessionStorage.getItem('userData'));
		const userId = data.userId;
		if (userId) {
			$('#journalistIds').val(userId).trigger('change');
		}
	}

}

// 表單提交事件處理
const handleSubmit = async (event) => {

	event.preventDefault();

	const file = $('#fileInput');
	if (!file.data('base64') && file[0].files.length === 0) {
		Swal.fire('錯誤', '請選擇一張照片', 'error');
		return;
	}
	const formData = {
		title: $('#title').val(),
		tagId: $('#tags').val(),
		content: tinymce.get('content').getContent(),
		userId: JSON.parse(sessionStorage.getItem('userData')).userId,
		image: $('#fileInput').data('base64'), // Base64 字串
		journalistIds: $('#journalistIds').val()
	};

	const action = $('#submit-btn').text() === '新增文章' ? submitPost : updatePost;
	await action(formData);
};

const submitPost = async (formData) => {
	await postData('/emp/news', 'POST', formData);
};

const updatePost = async (formData) => {
	const newsId = JSON.parse(sessionStorage.getItem('newsData')).newsId;
	await postData(`/emp/news/${newsId}`, 'PUT', formData);
	sessionStorage.removeItem('newsData');
};

const postData = async (endpoint, method, formData) => {
	try {
		const response = await fetch(`http://${ip}:8080/tinglinews${endpoint}`, {
			method,
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(formData)
		});

		const { state, message, data } = await response.json();

		if (state) {
			Swal.fire(message, '', 'success');
			setTimeout(() => {
				window.location.replace('/tinglinews/emp/index.html');
			}, 1000);
		} else {
			Swal.fire(message, '', 'warning');
		}
	} catch (error) {
		console.error(`${method === 'POST' ? '新增' : '修改'}文章錯誤：`, error);
		Swal.fire(`${method === 'POST' ? '新增' : '修改'}文章錯誤 請稍後再試`, '', 'error');
	}
};

const handleFileChange = function () {

	const file = this.files[0];

	if (file) {
		if (this.files.length > 1) {
			showErrorAndReset('只能上傳一張照片');
			return;
		}
		const maxFileSize = 1024 * 1024; // 1MB
		if (file.size > maxFileSize) {
			showErrorAndReset('檔案大小不能超過 1MB');
			return;
		}
		const reader = new FileReader();
		reader.onload = function (event) {
			const base64String = event.target.result.split(',')[1]; // Base64 字串
			$('#fileInput').data('base64', base64String); // 將 Base64 字串儲存在 fileInput 元素上
			$('#imgArea').html(`<img src="${event.target.result}" width="200" class="m-2">`);
		};
		reader.readAsDataURL(file);
	}
};

const showErrorAndReset = (message) => {
	Swal.fire('錯誤', message, 'error');
	$('#fileInput').val('');
};

const setEditorImage = (image) => {
	if (image) {
		const imageFormat = image.startsWith('/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAoHBwkHBgoICAgLCg8LDhgQDg0NDh0VFhEYITIjJh0pKycyMTI0GyUoKDcwJzgsLCkqLjYxNTU1HyY3Pi0zP') ? 'png' : 'jpeg';
		$('#imgArea').html(`<img src="data:image/${imageFormat};base64,${image}" width="200" class="m-2">`);
		$('#fileInput').data('base64', image);
	}
};

$(document).ready(() => {

	const userData = JSON.parse(sessionStorage.getItem('userData'));

	if (!userData) {
		window.location.replace('/tinglinews/emp/login.html');
		return;
	}

	loadTagOptions();
	loadJournalistOptions();

	// 內文編輯器
	tinymce.init({
		selector: '#content',
		language: 'zh_TW',
		content_css: `http://${ip}:8080/tinglinews/css/news.css`,
		plugins: 'autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount linkchecker',
		toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | a11ycheck | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
		// 確保編輯器完全初始化後才執行（不然會有錯誤）
		setup: (editor) => {
			editor.on('init', () => {
				// 如果 sessionStorage 中有 newsData 就是修改文章
				const data = JSON.parse(sessionStorage.getItem('newsData'));
				if (data) {
					$('#title').val(data.title);
					$('#tags').val(data.tagId);
					editor.setContent(data.content);
					setEditorImage(data.image);
					$('#journalistIds').val(data.journalistIds).trigger('change');
					$('#submit-btn').text('修改');
				};
			});
		}
	});

	// 表單提交
	$('#post-form').on('submit', handleSubmit);
	// 上傳圖片
	$('#fileInput').on('change', handleFileChange);
	
	$('#back').on('click', () => {
		sessionStorage.removeItem('newsData');
	})


});

