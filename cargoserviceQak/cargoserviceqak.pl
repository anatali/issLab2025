%====================================================================================
% cargoserviceqak description   
%====================================================================================
mqttBroker("localhost", "1883", "cargoevents").
request( createProduct, product(String) ).
reply( createdProduct, productid(ID) ).  %%for createProduct
request( deleteProduct, product(ID) ).
reply( deletedProduct, product(String) ).  %%for deleteProduct
request( getProduct, product(ID) ).
reply( getProductAnswer, product(JSonString) ).  %%for getProduct
request( getAllProducts, dummy(ID) ).
reply( getAllProductsAnswer, products(String) ).  %%for getAllProducts
dispatch( cargoinfo, cargoinfo(INFO) ).
event( cargoevent, cargoevent(INFO) ).
event( alarm, alarm(X) ).
%====================================================================================
context(ctxcargoservice, "localhost",  "TCP", "8111").
 qactor( exec_createdelete, ctxcargoservice, "it.unibo.exec_createdelete.Exec_createdelete").
 static(exec_createdelete).
  qactor( productservice, ctxcargoservice, "it.unibo.productservice.Productservice").
 static(productservice).
  qactor( exec_get, ctxcargoservice, "it.unibo.exec_get.Exec_get").
dynamic(exec_get). %%Oct2023 
  qactor( exec_getall, ctxcargoservice, "it.unibo.exec_getall.Exec_getall").
dynamic(exec_getall). %%Oct2023 
