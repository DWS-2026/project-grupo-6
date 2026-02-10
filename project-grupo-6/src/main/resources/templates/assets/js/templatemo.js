/*

TemplateMo 559 Zay Shop

https://templatemo.com/tm-559-zay-shop

*/

'use strict';
$(document).ready(function() {

    // Accordion
    var all_panels = $('.templatemo-accordion > li > ul');
    
    // Initialize accordion - show first panel, hide others
    all_panels.each(function(index) {
        if(index === 0) {
            $(this).addClass('active').slideDown(300);
        } else {
            $(this).slideUp(0);
        }
    });

    $('.templatemo-accordion > li > a').click(function() {
        var target = $(this).next();
        var icon = $(this).find('i');
        
        if(target.hasClass('active')){
            // Close the section with smooth animation
            target.removeClass('active').slideUp(300, function() {
                // Rotate icon back
                icon.css('transform', 'rotate(0deg)');
            });
        } else {
            // Close all other sections
            all_panels.not(target).removeClass('active').slideUp(300, function() {
                $(this).prev('a').find('i').css('transform', 'rotate(0deg)');
            });
            
            // Open the clicked section
            target.addClass('active').slideDown(300, function() {
                // Rotate icon down
                icon.css('transform', 'rotate(180deg)');
            });
        }
        return false;
    });
    // End accordion

    // Product detail
    $('.product-links-wap a').click(function(){
      var this_src = $(this).children('img').attr('src');
      $('#product-detail').attr('src',this_src);
      return false;
    });
    $('#btn-minus').click(function(){
      var val = $("#var-value").html();
      val = (val=='1')?val:val-1;
      $("#var-value").html(val);
      $("#product-quanity").val(val);
      return false;
    });
    $('#btn-plus').click(function(){
      var val = $("#var-value").html();
      val++;
      $("#var-value").html(val);
      $("#product-quanity").val(val);
      return false;
    });
    $('.btn-size').click(function(){
      var this_val = $(this).html();
      $("#product-size").val(this_val);
      $(".btn-size").removeClass('btn-secondary');
      $(".btn-size").addClass('btn-success');
      $(this).removeClass('btn-success');
      $(this).addClass('btn-secondary');
      return false;
    });
    // End roduct detail

});
