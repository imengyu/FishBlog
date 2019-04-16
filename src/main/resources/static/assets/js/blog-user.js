var main = new Vue({
    el: '#main',
    data: {
        currentUser: null,
        currentUserLoadStatus: 'notload',
        currentUserLoadError: ''
    },
    methods: {
        loadUser: function () {
            var currentUserId = getQueryString('user');
            if (currentUserId == null) currentUserId = getLastUrlAgrs(location.href).arg;
            if(!isNumber(currentUserId)) currentUserId = '0';
            var url = blog_api_address + "user/" + currentUserId;
            $.ajax({
                url: url,
                success: function (response) {
                    if (response.success) {
                        main.currentUserLoadStatus = 'loaded';
                        main.currentUser = response.data;
                    } else {
                        main.currentUserLoadStatus = 'failed';
                        main.currentUserLoadError = response.message;
                    }
                }, error: function (xhr, err) {
                    main.currentUserLoadStatus = 'failed';
                    main.currentUserLoadError = err;
                }
            });
        },
        getUserHead() {
            if (isNullOrEmpty(main.currentUser.headimg)) return "/images/default/head-default.png"
            else return getImageUrlFormHash(main.currentUser.headimg);
        },
        getUserCardBackground() {
            if (isNullOrEmpty(main.currentUser.cardBackground)) return "/images/background/mebg.jpg"
            else return getImageUrlFormHash(main.currentUser.cardBackground);
        },
    }
})


setLoaderFinishCallback(function () {
    main.loadUser();
})