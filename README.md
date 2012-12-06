RSImage
=======

An open source Android framework for Renderscript-based image processing framework based off GPUImage for iOS.

Library project and Sample project included. More Documentation and more effects as I continue to port the effects over.


Live Preview
In the sample application you can turn on live camera preview, if you decide to use live camera one thing to note is that you want the smallest possible size that works for your application. Because at the highest resolution (at the moment) you could have 2.5 million pixels that need to be rendered twice because we need to convert to rgb from NV21. Thats 5 million pixels for one effect. 