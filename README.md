RSImage
=======

An open source Android framework for Renderscript-based image processing framework based off GPUImage for iOS.

Library project and Sample project included. More Documentation and more effects as I continue to port the effects over.


Live Preview
In the sample application you can turn on live camera preview, if you decide to use live camera one thing to note is that you want the smallest possible size that works for your application. Because at the highest resolution (at the moment) you could have 2.5 million pixels that need to be rendered twice because we need to convert to rgb from NV21. Thats 5 million pixels for one effect. 


License
-------

   Copyright 2012 Cesar Aguilar

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.