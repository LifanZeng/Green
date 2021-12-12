$(document).ready(function($) {
	"use strict";

	$('.isotope-grid-post').each(function() {
		var $this = $(this);
		var $filter = $this.parent().find('.fount-folio-filter');
		$this.imagesLoaded(function() {
			$this.isotope({
				itemSelector: '.folio-item',
				layoutMode: 'fitRows',
			});
		});

		$filter.find('a').on("click", function(e) {
			e.preventDefault();
			$filter.find("a").removeClass('active');
			$(this).addClass('active');
			var data_filter = $(this).data('filter');
			$this.isotope({
				filter: data_filter
			});
		});
	});
	$('.isotope-masonry-post').each(function() {
		var $this = $(this);
		var $filter = $this.parent().find('.fount-folio-filter');
		$this.imagesLoaded(function() {
			$this.isotope({
				itemSelector: '.folio-item',
			});
		});
		$filter.find('a').click(function(e) {
			e.preventDefault();
			$filter.find("a").removeClass('active');
			$(this).addClass('active');
			var data_filter = $(this).data('filter');
			$this.isotope({
				filter: data_filter
			});
		});
	});
	$('.isotope-masonry-blog').each(function() {
		var $this = $(this);
		var $filter = $this.parent().find('.fount-folio-filter');
		$this.imagesLoaded(function() {
			$this.isotope({
				itemSelector: '.masonry-post-item',
			});
		});
		$filter.find('a').click(function(e) {
			e.preventDefault();
			$filter.find("a").removeClass('active');
			$(this).addClass('active');
			var data_filter = $(this).data('filter');
			$this.isotope({
				filter: data_filter
			});
		});
	});
});
