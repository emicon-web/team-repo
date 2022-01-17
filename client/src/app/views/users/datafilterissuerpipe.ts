import * as _ from 'lodash';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dataFilterIssuer'
})
export class DataFilterIssuerPipe implements PipeTransform {

  transform(array: any[], query: string): any {
    if (query) {
      return _.filter(array, row => row.issuerId != null && !(row.issuerId.search(RegExp(query, 'i')) === -1));
    }
    return array;
  }
}
