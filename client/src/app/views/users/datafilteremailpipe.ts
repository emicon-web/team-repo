import * as _ from 'lodash';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dataFilterEmail'
})
export class DataFilterEmailPipe implements PipeTransform {

  transform(array: any[], query: string): any {
    if (query) {
      return _.filter(array, row => row.userEmail != null && !(row.userEmail.search(RegExp(query, 'i')) === -1));
    }
    return array;
  }
}

