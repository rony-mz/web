$('.navbar li a').hover(function() {
    $(this).animate({ paddingLeft: '20px' }, 200);
}, function() {
    $(this).animate({ paddingLeft: '10px' }, 200);
});

$(window).on("scroll", function () {
    $(".ofert-1").each(function () {
        const top = $(this).offset().top;
        const scroll = $(window).scrollTop();
        const height = $(window).height();
        if (scroll + height > top + 100) {
            $(this).fadeIn(800);
        }
    });
});


$(".btn-2, .btn-1").hover(
    function () {
        $(this).css({
            backgroundColor: "#e74c3c",
            transform: "scale(1.1)",
            transition: "all 0.3s ease"
        });
    },
    function () {
        $(this).css({
            backgroundColor: "",
            transform: "scale(1)",
            transition: "all 0.3s ease"
        });
    }
);

$(document).ready(function () {
    $("img").hover(
        function () {
            $(this).css({
                transform: "scale(1.05)",
                transition: "transform 0.3s ease"
            });
        },
        function () {
            $(this).css({
                transform: "scale(1)",
                transition: "transform 0.3s ease"
            });
        }
    );
});


$(document).ready(function () {
    const imagenes = [
        "img/masterdg6aekaj7lbee5hbc6f49acic.jpg",
        "img/gastronomia.jpg",
        "img/peruana.png"
    ];

    let indice = 0;

    setInterval(function () {
        indice = (indice + 1) % imagenes.length;

        const nuevaImagen = `linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('${imagenes[indice]}')`;

        $(".header").css("background-image", nuevaImagen);
    }, 3000); 
});

$(document).ready(function() {
    $('.service-img img').on('mouseenter', function() {
        const $img = $(this);
        $img.stop(true); // Detiene animaciones anteriores

        // Efecto de destello (fade in / fade out rápido)
        $img.animate({ opacity: 0.4 }, 150)
            .animate({ opacity: 1 }, 150)
            .animate({ opacity: 0.4 }, 150)
            .animate({ opacity: 1 }, 150);
    });
});


$(document).ready(function() {
    $('.service-btn').on('mouseenter', function() {
        const $btn = $(this);
        $btn.stop(true).animate({ transformScale: 1.1 }, {
            step: function(now, fx) {
                if (fx.prop === "transformScale") {
                    $(this).css('transform', 'scale(' + now + ')');
                }
            },
            duration: 100
        }).animate({ transformScale: 1 }, {
            step: function(now, fx) {
                if (fx.prop === "transformScale") {
                    $(this).css('transform', 'scale(' + now + ')');
                }
            },
            duration: 100
        });
    });
});


$(document).ready(function () {
    $('.product-img img').hover(
        function () {
            $(this).css({
                'transform': 'scale(2.1)',
                'transition': 'transform 0.3s ease'
            });
        },
        function () {
            $(this).css({
                'transform': 'scale(1)',
                'transition': 'transform 0.3s ease'
            });
        }
    );
});




